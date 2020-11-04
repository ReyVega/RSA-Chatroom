import java.math.BigInteger;

public class Tuple3 {
	private BigInteger x, y, gcd;

	public Tuple3(BigInteger gcd, BigInteger x, BigInteger y) {
		this.gcd = gcd;
		this.x = x;
		this.y = y;
	}

	public BigInteger getGCD() {
		return this.gcd;
	}

	public BigInteger getX() {
		return this.x;
	}

	public BigInteger getY() {
		return this.y;
	}

	public void setGCD(BigInteger gcd) {
		this.gcd = gcd;
	}

	public void setX(BigInteger x) {
		this.x = x;
	}

	public void setY(BigInteger y) {
		this.y = y;
	}
}
