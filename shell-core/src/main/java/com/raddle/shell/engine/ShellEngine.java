package com.raddle.shell.engine;

/**
 * description: 
 * @author raddle
 * time : 2015年12月28日 下午11:02:50
 */
public interface ShellEngine {
    public void executeShell(String commandSessionId, String commandCode, Object[] params, ShellResponse response);

    public void stopShell(String commandSessionId, Object[] params, ShellResponse response);
}
