package at.fhv.sysarch.lab3.pipeline.pull;

// This interface will be implemented by the Pipe and the Filter. So that they can just call the pull() method of the predecessor
// Source               <- Pipe             <- Filter           <- Pipe             <- Sink
// return source data   <- source.pull()    <- source.pull()    <- source.pull()    <- source.pull()
public interface IPull<R> {

    R pull();

}
