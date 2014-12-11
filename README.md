# lumen

A WIP tiny programming language for the JVM.


### What Can It Do?

Not much... yet.

**Source**
```
class Example : Superclass +(Interface, AnotherInterface) {
    pv stringField: String
    
    pb def foo() {
        var hello: String = "Hello, world!"
    }
}
```

**Generated <code><strong>class</strong></code> file** (decompiled in IntelliJ)

![](http://i.imgur.com/mRJZR5l.png)


#### To Do
- [x] Finish field parsing (done)
- [ ] Method parsing (doing)
- [ ] Semantic analysis
- [ ] Type checker
- [ ] Bytecode generation
