package ch.ethz.idsc.tensor.io.ext;

import java.io.Serializable;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;

/* package */ class WavefrontObjectImpl implements WavefrontObject, Serializable {
	private final String string;
	private final IASTAppendable faces = F.ListAlloc();
	private final IASTAppendable normals = F.ListAlloc();

	public WavefrontObjectImpl(String string) {
		this.string = string;
	}

	@Override // from WavefrontObject
	public String name() {
		return string;
	}

	@Override // from WavefrontObject
	public IAST faces() {
		return faces;
	}

	@Override // from WavefrontObject
	public IAST normals() {
		return normals;
	}

	void append_f(String line) {
		String[] nodes = line.split(" +");
		IASTAppendable iv = F.ListAlloc();
		IASTAppendable in = F.ListAlloc();
		for (int index = 0; index < nodes.length; ++index) {
			String[] node = nodes[index].split("/");
			iv.append(F.QQ(Integer.parseInt(node[0]) - 1, 1));
			in.append(F.QQ(Integer.parseInt(node[2]) - 1, 1));
		}
		faces.append(iv);
		normals.append(in);
	}
}
