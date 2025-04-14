# Understanding Write-Ahead Logs: Durability Beyond the Flush

**Dive into the internals of how modern databases use WAL to ensure your data survives crashes and reboots.**

This project demonstrates a **simple Key-Value Store** with a **Write-Ahead Log (WAL)** to demonstrate core concepts of durability and crash recovery used in database systems.

---

## Features

- Durable writes using **Write-Ahead Log**
- Automatic **recovery** on restart
- Simple CLI to interact: `put`, `get`, `print`, `exit`
- Includes **JUnit 5 tests**
---

## Getting Started

### Clone the repository
```jshelllanguage
git clone https://github.com/sbcharr/java-write-ahead-log.git
cd java-write-ahead-log
```

### Build the project
```jshelllanguage
mvn clean compile
```

### Run the app
mvn exec:java -Dexec.mainClass="com.github.sbcharr.App"

### Run tests
```jshelllanguage
mvn test
```
Tested scenarios include:

* Put/Get functionality

* WAL append and recovery

* Handling of corrupted or partial logs (TBD)

### Sample CLI Usage
```jshelllanguage
> put foo bar
OK
> get foo
Value: bar
> print
foo=bar
> exit
```

### Key Concepts Covered
* WAL Fundamentals: Always write to the log before updating in-memory structures.

* Crash Recovery: Replay WAL to restore the state after a failure.

* Durability: Uses flush() to ensure data is physically written to disk.

* Append-only file: Simplified write logic and crash recovery.

### Contributions Welcome!
This is a learning project. Feel free to fork, play around, and improve it.