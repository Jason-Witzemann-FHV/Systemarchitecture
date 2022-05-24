package at.fhv.sysarch.lab3.pipeline.push;

// This interface will be implemented by the Pipe and the Filter. So that they can just call the push() method to push to the successor.
// Source               -> Pipe             -> Filter           -> Pipe             -> Sink
// successor.push()     -> successor.push() -> successor.push() -> successor.push() -> display the data
public interface IPush/*myself off the bridge*/<E> {
    void push(E element);
}
