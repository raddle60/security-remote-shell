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

import com.raddle.shell.engine.ShellEngine;

/**
 * description: 
 * @author raddle
 * time : 2015年12月28日 下午11:03:48
 */
public class JsShellEngine implements ShellEngine {
    private Map<String, ScriptEngine> commanders = new HashMap<>();
    private String commandDir;

    public void init(String commandDir) {
        this.commandDir = commandDir;
        Collection<File> listFiles = FileUtils.listFiles(new File(commandDir), new String[] { "js" }, true);
    }

    @Override
    public Object executeShell(String command, Object[] params) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");
        try {
            nashorn.put("jsShellEngine", this);
            nashorn.put("jsEngine", nashorn);
            nashorn.eval("function include(filePath){jsShellEngine.includeJs(jsEngine,filePath)}");
            nashorn.put("javax.script.filename", "test2.js");
            nashorn.eval(new InputStreamReader(new FileInputStream("src/test/resources/test2.js"), "utf-8"));
            System.out.println(nashorn.eval("getBB()"));
            System.out.println(nashorn.eval("cc"));
            Invocable jsInvoke = (Invocable) nashorn;
            jsInvoke.invokeFunction("incr", new Object[] { 8 });
            System.out.println(nashorn.eval("dd"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void includeJs(ScriptEngine nashorn, String filePath) throws UnsupportedEncodingException, FileNotFoundException, ScriptException {
        nashorn.put("javax.script.filename", FilenameUtils.getName(filePath));
        nashorn.eval(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
    }

}
