package com.raddle.shell.engine;

/**
 * description: 
 * @author raddle
 * time : 2015年12月28日 下午11:02:50
 */
public interface ShellEngine {
    public Object executeShell(String command, Object[] params);
}
