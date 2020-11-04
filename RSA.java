import java.math.BigInteger;
import java.util.ArrayList;

public class RSA {
	public static class Tuple3 {
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

	// Método para obtener el máximo común divisor (Algoritmo euclideano básico)
	public static BigInteger gcd(BigInteger a, BigInteger b) {
		if (a.compareTo(BigInteger.valueOf(0)) == 0) {
			return b;
		} else {
			return gcd(b.mod(a), a);
		}
	}

	// Método para obtener el máximo común divisor (Algoritmo euclideano extendido)
	// ax + by = gcd(a, b)
	public static Tuple3 gcdExtended(BigInteger a, BigInteger b) {
		if (a.compareTo(BigInteger.valueOf(0)) == 0) {
			return new Tuple3(b, BigInteger.valueOf(0), BigInteger.valueOf(1));
		} else {
			BigInteger x = BigInteger.valueOf(1),
					   y = BigInteger.valueOf(1);
			Tuple3 gcd = gcdExtended(b.mod(a), a);
			
			gcd.setX(y.subtract(b.divide(a)).multiply(x));
			gcd.setY(x);
			return gcd;
		}
	}

	public static ArrayList<BigInteger> encriptar(String mensaje, BigInteger e, BigInteger n) {
		ArrayList<BigInteger> resultado = new ArrayList<BigInteger>();

		// (ch^e) mod n
		for (Character ch : mensaje.toCharArray()) {
			BigInteger base = BigInteger.valueOf((int) ch - 32);
			BigInteger exponent = e;
			BigInteger mod = n;
			base.modPow(exponent, mod);

			resultado.add(base);
		}
		return resultado;
	}

	public static String desencriptar(ArrayList<BigInteger> mensaje, BigInteger d, BigInteger n) {
		String resultado = "";
		// (ch^d) mod n
		// Nota: ch ya está encriptado
		for (BigInteger num : mensaje) {
			BigInteger base = num;
			BigInteger exponent = d;
			BigInteger mod = n;
			base.modPow(exponent, mod);

			char character = (char) (base.intValue() + 32);
			resultado += character;
		}
		return resultado;
	}

	public static void main(String[] args) {
		// p y q son números primos
		BigInteger p = BigInteger.valueOf(3), q = BigInteger.valueOf(7);

		// Primera parte por hacer para obtener la llave pública
		// n será el módulo a usar para encriptar y desencriptar mensajes
		// n = p * q
		BigInteger n = p.multiply(q);

		// Calcular phi que es la segunda parte para obtener la llave pública
		// "e" será la llave pública y sufrirá cambios al encontrar el coprimo
		// phi = (p - 1) * (q - 1)
		BigInteger phi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
		BigInteger e = BigInteger.valueOf(2);

		// "e" tienes que ser menor y coprimo de phi
		// e < phi
		// -1 less, 0 equal, 1 greater
		while (e.compareTo(phi) == -1) {
			if (gcd(e, phi).compareTo(BigInteger.valueOf(1)) == 0) {
				break;
			} else {
				e = e.add(BigInteger.valueOf(1));
			}
		}

		// Obtención de la llave privada
		BigInteger d = gcdExtended(e, phi).getX().mod(phi);

		String msg = "a ver hola";

		ArrayList<BigInteger> h = encriptar(msg, e, n);
		System.out.println(h);
//		System.out.println(desencriptar(h, d, n));
	}
}
