# lumen

A WIP tiny programming language for the JVM.


### What Can It Do?

Not much... yet. So far, only basic class and field definitions:

**Source**
```
pv class Example : Superclass +(Interface, AnotherInterface) {
    pb intField: int
    pv anotherField: double
}
```

**Generated <code><strong>class</strong></code> file** (decompiled in IntelliJ)

![](http://f.cl.ly/items/2v3D151U1n1M1h3k3C3N/Image%202014-11-20%20at%2011.56.18%20PM.png)


#### To Do
- [x] Finish field parsing (done)
- [ ] Method parsing (doing)
- [ ] Semantic analysis
- [ ] Type checker
- [ ] Bytecode generation
