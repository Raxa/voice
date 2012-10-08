package raxa.ivr.core.utils;

public class ConcatenateArrays {
	public ConcatenateArrays() {
		
	}
	
	public byte[] concat(byte[] first, byte[] second) {
		byte[] x = new byte[first.length + second.length];
		System.arraycopy(first, 0, x, 0, first.length);
		System.arraycopy(second, 0, x, first.length, second.length);
		return x;
	}
}
