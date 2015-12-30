package com.raddle.shell.engine.impl;

import org.junit.BeforeClass;
import org.junit.Test;

public class JsShellEngineTest {
    private static JsShellEngine engine = new JsShellEngine();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testExecuteShell() {
        engine.init("src/test/resources/commander");
        engine.executeShell(null, null, null, null);
    }

}
