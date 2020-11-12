import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {

	// Variables globales
	private Socket cliente;
	private DataInputStream input;
	private DataOutputStream output;
	private String nombre;
	private BigInteger n;
	private BigInteger e;
	private BigInteger d;

	public ClientHandler(Socket client, DataInputStream input, DataOutputStream output, String nombre, BigInteger n,
			BigInteger e, BigInteger d) {
		this.cliente = client;
		this.input = input;
		this.output = output;
		this.nombre = nombre;

		// Módulo
		this.n = n;

		// Llave pública
		this.e = e;

		// Llave privada
		this.d = d;

		// Mostrarle a todos los clientes quien se conecto
		try {
			for (ClientHandler ch : Server.clientes) {
				if (!ch.equals(this)) {
					ch.output.writeUTF(this.nombre + " conectado");
				}
			}
		} catch (IOException s) {
			s.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Mensaje recibido de cualquier cliente
		String recibido = "";
		try {
			while (true) {
				recibido = this.input.readUTF();
				// Mensajes privados
				if (recibido.contains("#")) {
					StringTokenizer st = new StringTokenizer(recibido, "#");
					String mensajePrivado = st.nextToken();
					String persona = st.nextToken();

					for (ClientHandler ch : Server.clientes) {
						if (ch.nombre.equals(persona)) {
							ArrayList<BigInteger> msgEncriptado = encriptar(mensajePrivado, ch.getLlavePublica(),
									ch.getModulo());
							System.out.println("> " + this.nombre + ": " + msgEncriptado);

							String msgDescifrado = ch.desencriptar(msgEncriptado, ch.getLlavePrivada(), ch.getModulo());
							ch.output.writeUTF(this.nombre + ": " + msgDescifrado);
							System.out.println("> " + this.nombre + ": " + msgDescifrado);
						}
					}
					// Mensajes públicos
				} else {
					// Mandar mensaje del cliente a todos menos a él mismo
					for (ClientHandler ch : Server.clientes) {
						if (!ch.equals(this)) {
							// Encriptar mensaje dependiendo del cliente al que se le envíe
							ArrayList<BigInteger> msgEncriptado = encriptar(recibido, ch.getLlavePublica(),
									ch.getModulo());
							System.out.println("> " + this.nombre + ": " + msgEncriptado);

							// Descifrar mensaje desde la consola del propietario de la llave privada
							String msgDescifrado = ch.desencriptar(msgEncriptado, ch.getLlavePrivada(), ch.getModulo());
							ch.output.writeUTF(this.nombre + ": " + msgDescifrado);
							System.out.println("> " + this.nombre + ": " + msgDescifrado);
						}
					}
				}
			}
		} catch (IOException e) {
			// Si se desconecta el cliente, borrarlo del servidor y mostrar que se
			// desconectó
			try {
				this.cliente.close();
				this.input.close();
				this.output.close();
				Server.clientes.remove(this);

				System.out.println("> " + this.nombre + " desconectado");
				for (ClientHandler ch : Server.clientes) {
					ch.output.writeUTF(this.nombre + " desconectado");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				this.finalize();
			} catch (Throwable e1) {
				e1.printStackTrace();
			}
		}
	}

	// Exponenciación rápida
	public static BigInteger fastExponentiation(BigInteger a, BigInteger b, BigInteger m) {
		BigInteger result = BigInteger.valueOf(1);
		// while(b > 0)
		while (b.compareTo(BigInteger.valueOf(0)) == 1) {
			// if(b % 2 == 0), checamos si el dígito binario es 1
			if (b.mod(BigInteger.valueOf(2)).compareTo(BigInteger.valueOf(1)) == 0) {
				result = result.multiply(a).mod(m);
			}
			a = a.multiply(a).mod(m);
			// Dividimos para sacar los dígitos del número binario
			b = b.divide(BigInteger.valueOf(2));
		}
		return result;
	}

	public static ArrayList<BigInteger> encriptar(String mensaje, BigInteger e, BigInteger n) {
		ArrayList<BigInteger> resultado = new ArrayList<BigInteger>();

		// (ch^e) mod n
		for (Character ch : mensaje.toCharArray()) {
			BigInteger base = BigInteger.valueOf((int) ch);
			BigInteger exponent = e;
			BigInteger mod = n;
			BigInteger c = fastExponentiation(base, exponent, mod);

			resultado.add(c);
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
			BigInteger c = fastExponentiation(base, exponent, mod);

			char character = (char) (c.intValue());
			resultado += character;
		}
		return resultado;
	}

	public BigInteger getLlavePublica() {
		return this.e;
	}

	public BigInteger getModulo() {
		return this.n;
	}

	public BigInteger getLlavePrivada() {
		return this.d;
	}
}
