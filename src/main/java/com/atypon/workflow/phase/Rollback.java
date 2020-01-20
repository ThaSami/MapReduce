package com.atypon.workflow.phase;

import com.atypon.workflow.Context;

public interface Rollback {
    void rollback(Context context);
}
