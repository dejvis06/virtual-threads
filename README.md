# 🧵 Virtual Threads vs Platform Threads — Conceptual Comparison

This project demonstrates the **fundamental difference in execution models**
between **virtual threads** and **platform threads** in the JVM

---

## ⚡ Virtual Threads (JVM-managed)

Virtual threads are **lightweight threads managed by the JVM**.

- Millions of virtual threads can be created
- Many virtual threads share a small number of OS threads
- The JVM handles scheduling and blocking
- OS thread creation and context switching are minimized

---

## 🧱 Platform Threads (OS-managed)

Platform threads map **1:1 to native OS threads**.

- Each thread requires a real operating system thread
- The OS is responsible for thread creation, scheduling, and context switching
- Large thread counts introduce significant overhead
- At very high counts, systems may slow dramatically or fail

This model works well for smaller thread counts but does not scale
efficiently to hundreds of thousands or millions of threads.

---

## 📚 Reference

- Oracle Java Virtual Threads documentation  
  https://docs.oracle.com/en/java/javase/24/core/virtual-threads.html
