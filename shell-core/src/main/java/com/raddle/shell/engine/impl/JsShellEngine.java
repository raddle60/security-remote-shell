package com.raddle.shell.engine.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.raddle.shell.engine.ShellEngine;
import com.raddle.shell.engine.ShellResponse;

/**
 * description: 
 * @author raddle
 * time : 2015年12月28日 下午11:03:48
 */
public class JsShellEngine implements ShellEngine {
    private static final Logger logger = LoggerFactory.getLogger(JsShellEngine.class);
    private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private Map<String, ScriptEngine> commanders = new HashMap<>();
    private String commandDir;

    public void init(String commandDir) {
        logger.info("load js from " + new File(commandDir).getAbsolutePath());
        this.commandDir = commandDir;
        Map<String, ScriptEngine> tempCommanders = new HashMap<>();
        Collection<File> listFiles = FileUtils.listFiles(new File(commandDir), new String[] { "js" }, true);
        for (File file : listFiles) {
            ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
            nashorn.put("jsShellEngine", this);
            nashorn.put("jsEngine", nashorn);
            try {
                nashorn.put("javax.script.filename", file.getName());
                nashorn.eval("function include(filePath){jsShellEngine.includeJs(jsEngine,filePath)}");
                nashorn.eval(new InputStreamReader(new FileInputStream(file), "utf-8"));
                // 检查定义
                Object commanderDefinition = nashorn.eval("typeof commanderDefinition");
                if ("undefined".equals(commanderDefinition)) {
                    logger.error("ignore " + file.getName() + " commanderDefinition is undefined");
                    continue;
                }
                String code = (String) nashorn.eval("commanderDefinition.code");
                if (StringUtils.isEmpty(code)) {
                    logger.error("ignore " + file.getName() + ", code is empty");
                }
                String name = (String) nashorn.eval("commanderDefinition.name");
                if (StringUtils.isEmpty(name)) {
                    logger.error("ignore " + file.getName() + ", name is empty");
                }
                String desc = (String) nashorn.eval("commanderDefinition.desc");
                if (StringUtils.isEmpty(desc)) {
                    logger.error("ignore " + file.getName() + ", desc is empty");
                }
                // 检查方法
                Object executeCommand = nashorn.eval("typeof executeCommand");
                if ("undefined".equals(executeCommand)) {
                    logger.error("ignore " + file.getName() + " executeCommand is undefined");
                    continue;
                }
                if (!"function".equals(executeCommand)) {
                    logger.error("ignore " + file.getName() + " executeCommand is not function, but {}", executeCommand);
                    continue;
                }
                logger.info("loaded " + file.getName());
                tempCommanders.put(code, nashorn);
            } catch (Exception e) {
                logger.error("ignore " + file.getName() + " " + e.getMessage(), e);
            }
        }
        commanders = tempCommanders;
    }

    @Override
    public void executeShell(String commandSessionId, String commandCode, Object[] params, ShellResponse response) {
        if (!commanders.containsKey(commandCode)) {
            logger.error("command[" + commandCode + "] is not supported");
            response.writeMessage("command[" + commandCode + "] is not supported");
            response.writeExitCode(ShellResponse.EXIT_NOT_EXECUTED);
        }
        ScriptEngine scriptEngine = commanders.get(commandCode);
        try {
            Object invokeFunction = ((Invocable) scriptEngine).invokeFunction("executeCommand", new Object[] { commandSessionId, commandCode, params, response });
            if (invokeFunction != null) {
                if (invokeFunction instanceof Number) {
                    response.writeExitCode(((Number) invokeFunction).intValue());
                } else {
                    response.writeMessage("executeCommand not return unsuppored exit code , " + invokeFunction + "[" + invokeFunction.getClass() + "]");
                    response.writeExitCode(ShellResponse.EXIT_UNKNOWN_ERROR);
                }
            } else {
                response.writeMessage("executeCommand not return exit code");
                response.writeExitCode(ShellResponse.EXIT_UNKNOWN_ERROR);
            }
        } catch (Exception e) {
            logger.error("executeCommand exception", e);
            response.writeMessage("executeCommand exception , " + StringUtils.defaultIfEmpty(e.getMessage(), e.getClass().getName()));
            response.writeExitCode(ShellResponse.EXIT_UNKNOWN_ERROR);
        }
    }

    @Override
    public void stopShell(String commandSessionId, Object[] params, ShellResponse response) {
        response.writeMessage("not impliment yet");
        response.writeExitCode(ShellResponse.EXIT_NOT_EXECUTED);
    }

    public void includeJs(ScriptEngine nashorn, String filePath) throws UnsupportedEncodingException, FileNotFoundException, ScriptException {
        Object oldFile = nashorn.get("javax.script.filename");
        try {
            nashorn.put("javax.script.filename", FilenameUtils.getName(filePath));
            nashorn.eval(new InputStreamReader(new FileInputStream(new File(commandDir, filePath)), "utf-8"));
        } finally {
            nashorn.put("javax.script.filename", oldFile);
        }
    }

}
