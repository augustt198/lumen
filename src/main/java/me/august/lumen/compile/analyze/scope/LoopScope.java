package me.august.lumen.compile.analyze.scope;

import me.august.lumen.compile.ast.stmt.Loop;

public class LoopScope extends Scope {

    private Loop loop;

    public LoopScope(Scope parent, Loop loop) {
        super(parent, ScopeType.LOOP);
        this.loop = loop;
    }

    public Loop getLoop() {
        return loop;
    }

}
