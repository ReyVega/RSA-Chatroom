import java.math.BigInteger;
import java.util.ArrayList;

public class RSA {

	// Método para sacar el máximo común divisor
	public static double mcd(int num1, int num2) {
		int tmp;
		while (true) {
			tmp = num1 % num2;
			if (tmp == 0) {
				return num2;
			}
			num1 = num2;
			num2 = tmp;
		}
	}

	public static ArrayList<Integer> encriptar(String mensaje, int e, int n) {
		ArrayList<Integer> resultado = new ArrayList<Integer>();

		// (ch^e) mod n
		for (Character ch : mensaje.toCharArray()) {
			BigInteger base = BigInteger.valueOf((int) ch - 32);
			BigInteger exponent = BigInteger.valueOf(e);
			BigInteger mod = BigInteger.valueOf(n);
			base.modPow(exponent, mod);

			resultado.add(base.intValue());
		}
		return resultado;
	}

	public static String desencriptar(ArrayList<Integer> mensaje, int d, int n) {
		String resultado = "";
		// (ch^d) mod n
		// Nota: ch ya está encriptado
		for (Integer num : mensaje) {
			BigInteger base = BigInteger.valueOf(num);
			BigInteger exponent = BigInteger.valueOf(d);
			BigInteger mod = BigInteger.valueOf(n);
			base.modPow(exponent, mod);
			
			char character = (char) (base.intValue() + 32);
			resultado += character;
		}
		return resultado;
	}

	public static void main(String[] args) {
		// p y q son números primos
		int p = 3, q = 7;

		// Primera parte por hacer para obtener la llave pública
		// n será el módulo a usar para encriptar y desencriptar mensajes
		int n = p * q;

		// Calcular phi que es la segunda parte para obtener la llave pública
		// "e" será la llave pública y sufrirá cambios al encontrar el coprimo
		int phi = (p - 1) * (q - 1);
		int e = 2;

		// "e" tienes que ser menor y coprimo de phi
		while (e < phi) {
			if (mcd(e, phi) == 1) {
				break;
			} else {
				e++;
			}
		}

		// Obtención de la llave privada
		int k = 0;
		double dtmp = 0;

		do {
			dtmp = (1.0 + (k * phi)) / e;
			k++;
		// Hasta que d sea un numero entero
		} while (dtmp % 1 != 0);
		int d = (int) dtmp;

		String msg = "hola que tal uwu x z ari v";
		
		ArrayList<Integer> h = encriptar(msg, e, n);
		System.out.println(h);
		System.out.println(desencriptar(h,d,n));
	}
}
