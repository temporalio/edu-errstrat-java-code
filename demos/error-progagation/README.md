# Error Propagation Demo

This demo demonstrates the Temporal's ability to preserve stack traces across
process and SDK language boundaries. Temporal's use of a single Protobuf to 
represent failure makes preserving these easy.

**Note: Running this demo will require you to have Python, Java, and Go installed
on your machine. These are already provided in the GitPod environment**

## Demo Explanation

This code demonstrates the propagation of errors across language and process
boundaries. A Go program requests the execution of a Java Workflow that calls
an Activity in Python to compose a greeting. This greeting purposefully fails,
and the error messages are propagated back up to the starter, preserving the
stack traces and adding breadcrumbs so you can see the path it took.

Below is a diagram of how this process:

```mermaid
sequenceDiagram
    participant Go as Go Starter
    participant Java as Java Workflow
    participant Python as Python Activity

    Go->>Java: Start workflow
    Java->>Python: Execute activity
    Python-->>Java: Activity fails (Error)
    Java-->>Go: Propagate error
```

**Before running the below code, ensure you have a Temporal Service running**

## Run the Python Activity

1. Change directories into the `activity` directory
2. Create a virtual environment and activate it
```bash
python -m venv venv
source venv/bin/activate
```
3. Install the required Python packages
```bash
pip install -r requirements.txt
```
4. Run the Worker
```bash
python worker.py
```

## Run the Java Workflow

1. Change directories into the `workflow` directory.
2. Compile the Java Workflow.
```bash
mvn clean compile
```
3. Run the Worker using `mvn`
```bash
mvn exec:java -Dexec.mainClass="greetingworkflow.GreetingWorker"
```

## Run the Go Starter

1. Change directories into the `workflow` directory.
2. Install the Go SDK
```bash
go get
```
3. Run the Starter
```bash
go run main.go
```

## Observing the Results

1. Review the stack trace in the terminal that runs the Python Worker. It should
only show the Python code.
2. Review the stack trace in the terminal window that runs the Java Worker. You 
should see the stack trace from the Python Activity in the Java stack trace.
3. Observe the stack trace from the Starter code. Note that it shows the messages
from the failures from both the Java Workflow and Python Activity.
4. Go to the Web UI. View the Event History and expand on the Failed events. You
should see the stack traces.