# Primer

Telemetry refers to data emitted from a system and its behavior. The data can come in the form of traces, metrics, and logs. 

Distributed tracing lets you observe requests as they propagate through complex, distributed systems. Distributed tracing improves the visibility of your application or system’s health and lets you debug behavior that is difficult to reproduce locally.

To understand distributed tracing, you need to understand the role of each of its components: logs, spans, and traces.
- Logs are self-explanatory. These are just normal logs that you encounter in any system.
- A span represents a unit of work or operation. Spans track specific operations that a request makes, painting a picture of what happened during the time in which that operation was executed. A span contains name, time-related data, structured log messages, and other metadata (that is, Attributes) to provide information about the operation it tracks.
- A trace is made of one or more spans. The first span represents the root span (which encapsulates its child spans and adds some of its own information). Each root span represents a request from start to finish. In a way, a trace is a tree of spans.

# Signals

Signals are system outputs that describe the underlying activity of the operating system and the applications running on a platform. Examples of signals are traces, metrics, logs, and baggage.

## Traces

Spans are correlated with one another by sharing the same trace_id and utilizing parent_id to represent parent-child span relationships. Traces are essentially a collection of structured logs with context, correlation, hierarchy, and more baked in.

Here are the components that will play a part in instrumenting our code for traces
- Tracer Provider. This is a factory for `Tracer`s, and also includes Resource and Exporter initialization. It is usually initialized once.
- A `Tracer` creates spans.
- A `Trace Exporter` sends traces to a consumer (like a backend for the traces).
- Context Propagation is the concept by which spans are correlated with each other.
- A span represents a unit of work or operation. They include:
  - Name
  - Parent span ID (empty for root spans)
  - Start and end timestamps
  - Span Context
    - Trace ID
    - span's Span ID
    - Trace flags (binary encoding containing info about the trace)
    - Trace state (list of key-values that can carry vendor-specific trace info)
  - Attributes
    - Metadata about the span. Additionally, there are semantic attributes, which are known naming conventions for metadata.
  - Span Events
    - A structured log message that denots a meaningful singular point in time during the span's duration
  - Span Links
    - To imply causal relationships (e.g. linking the last spam from one trace to the first span in another trace)
  - Span Status
    - "Unset": operation successfully completed.
    - "Error"
    - "Ok": Span was explicitly marked as error-free by a developer.
  - Span Kind
    - Represents a process boundary (e.g. the parent of an HTTP "server" span is usually an HTTP "client" span, and the additional child spans of the "server" span are usually "internal")
   
## Metrics

They are a measurement of a service captured at runtime.

## Logs

OpenTelemetry does not define a bespoke API or SDK to create logs. Instead, OpenTelemetry logs are the existing logs you already have from a logging framework or infrastructure component. OpenTelemetry SDKs and autoinstrumentation utilize several components to automatically correlate logs with traces.

Here are the components that are relevant to an application developer:
- Log Record Exporters send log records to a consumer. This consumer can be standard output for debugging and development-time, the OpenTelemetry Collector, or any open source or vendor backend of your choice.
- A log record represents the recording of an event. In OpenTelemetry a log record contains two kinds of fields:
  - Named top-level fields of specific type and meaning.
  - Resource and attributes fields of arbitrary value and type

## Baggage

In OpenTelemetry, Baggage is contextual information that resides next to context. Baggage is a key-value store, which means it lets you propagate any data you like alongside context.

Baggage means you can pass data across services and processes, making it available to add to traces, metrics, or logs in those services.

# Instrumentation

For a system to be observable, it must be instrumented: that is, code from the system’s components must emit signals, such as traces, metrics, and logs. There are 2 types of instrumentation:
- Zero-code instrumentation
  - Usually added as an agent or agent-like installation. Ranges from bytecode manipulation to monkey patching.
- Code-based
  - You need the OpenTelemetry API and possibly the SDK (if your artifact is a process or a service).

OpenTelemetry provides instrumentation libraries for many libraries, which is typically done through library hooks or monkey-patching library code.
