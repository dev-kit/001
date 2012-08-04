package com.bg.check.test;

import android.test.InstrumentationTestCase;

import com.bg.check.engine.TaskEngine;
import com.bg.check.engine.BaseTask;
import com.bg.check.engine.BaseTask.TaskCallback;

public class TaskEngineTest extends InstrumentationTestCase {

    public void testStartTaskEngine() {
        assertNotNull(TaskEngine.getInstance());
        assertEquals(TaskEngine.getInstance(), TaskEngine.getInstance());
    }

    public void testAppendNullTask() {
        try {
            TaskEngine.getInstance().appendTask(null);
        } catch (Exception e) {
            fail();
        }
    }

    public void testAppendTask() {
        final String testString = "1234";
        BaseTask task = new BaseTask() {
            @Override
            public void run() {
                setTaskName(testString);
            }
        };
        TaskEngine.getInstance().appendTask(task);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(testString, task.getTaskName());

        final BaseTask task2 = new BaseTask() {
            @Override
            public void run() {
            }
        };
        task2.setCallback(null);
        task2.setCallback(new TaskCallback() {

            @Override
            public void onCallBack() {
                task2.setTaskName(testString);
            }
        });
        TaskEngine.getInstance().appendTask(task2);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(testString, task2.getTaskName());
    }

    public void testAppendTasks() {
        final StringBuffer sb = new StringBuffer();
        int tasksNumber = 100;
        while (tasksNumber-- > 0) {
            final int count = tasksNumber;
            BaseTask task = new BaseTask() {
                @Override
                public void run() {
                    sb.append(String.valueOf(count));
                }
            };
            TaskEngine.getInstance().appendTask(task);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        StringBuffer expect = new StringBuffer();
        tasksNumber = 100;
        while (tasksNumber-- > 0) {
            expect.append(tasksNumber);
        }
        assertEquals(sb.toString(), expect.toString());
    }

    public void testCancelTask() {
        final BaseTask task2 = new BaseTask() {
            @Override
            public void run() {
                setTaskName("test");
            }
        };
        task2.setCallback(null);
        task2.setCallback(new TaskCallback() {

            @Override
            public void onCallBack() {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                task2.setTaskName("test");
            }
        });
        TaskEngine.getInstance().appendTask(task2);
        TaskEngine.getInstance().cancelTask(task2);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNull(task2.getTaskName());

    }
    
    
    public void testCancelAllTasks() {
        final StringBuffer sb = new StringBuffer();
        int tasksNumber = 100;
        while (tasksNumber-- > 0) {
            final int count = tasksNumber;
            BaseTask task = new BaseTask() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sb.append(String.valueOf(count));
                }
            };
            TaskEngine.getInstance().appendTask(task);
        }
        TaskEngine.getInstance().clearAllTasks();
        
        assertEquals(sb.toString(), "");
    }
}
