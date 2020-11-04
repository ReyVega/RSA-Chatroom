import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {

	// Variables globales
	private Socket socket;
	private ServerSocket serverSocket;
	private DataInputStream input;
	private DataOutputStream output;
	public static ArrayList<ClientHandler> clientes = new ArrayList<ClientHandler>();

	public Server(int port) {
		try {
			// Inicializar el servidor
			this.serverSocket = new ServerSocket(port);
			System.out.println("Servidor inicializado");

			// Correr ciclo para esperar clientes
			while (true) {
				// Esperar a que el cliente se conecte
				this.socket = this.serverSocket.accept();

				// Recibir mensajes del cliente
				this.input = new DataInputStream(this.socket.getInputStream());

				// Mandar mensajes al cliente
				this.output = new DataOutputStream(this.socket.getOutputStream());

				// Crear manejador para el cliente
				String nombre = "";

				while (nombre.equals("")) {
					nombre = this.input.readUTF();
				}
				System.out.println("> " + nombre + " conectado");
				ArrayList<BigInteger> tupla = generarLlavePublica();
				BigInteger n = tupla.get(0);
				BigInteger e = tupla.get(1);
				BigInteger d = generarLlavePrivada(tupla.get(1), tupla.get(2));
		
				ClientHandler manejador = new ClientHandler(this.socket, this.input, this.output, nombre, n, e, d);

				// Crear hilo para el cliente
				Thread hilo = new Thread(manejador);

				// Añadir hilo del cliente al servidor para ser manipulado y pasar llave pública
				clientes.add(manejador);

				// Empezar el hilo del cliente
				hilo.start();
			}

		} catch (SocketException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<BigInteger> generarLlavePublica() {
		// p y q son números primos
		BigInteger p = new BigInteger(PrimeNumbers.primeNumbers.get(0)), q = new BigInteger(PrimeNumbers.primeNumbers.get(1));
		PrimeNumbers.primeNumbers.remove(0);
		PrimeNumbers.primeNumbers.remove(0);

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
		ArrayList<BigInteger> tupla = new ArrayList<BigInteger>();
		tupla.add(n);
		tupla.add(e);
		tupla.add(phi);
		return tupla;
	}

	public static BigInteger gcd(BigInteger a, BigInteger b) {
		if (a.compareTo(BigInteger.valueOf(0)) == 0) {
			return b;
		} else {
			return gcd(b.mod(a), a);
		}
	}

	public BigInteger generarLlavePrivada(BigInteger e, BigInteger phi) {
		BigInteger d = gcdExtended(e, phi).getX().mod(phi);
		return d;
	}

	public Tuple3 gcdExtended(BigInteger a, BigInteger b) {
		if (a.compareTo(BigInteger.valueOf(0)) == 0) {
			return new Tuple3(b, BigInteger.valueOf(0), BigInteger.valueOf(1));
		} else {
			BigInteger x = BigInteger.valueOf(1), y = BigInteger.valueOf(1);
			Tuple3 gcd = gcdExtended(b.mod(a), a);

			gcd.setX(y.subtract(b.divide(a)).multiply(x));
			gcd.setY(x);
			return gcd;
		}
	}

	public static void main(String[] args) throws BindException {
		Server server = new Server(2727);
	}
}
