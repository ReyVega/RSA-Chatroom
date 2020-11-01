import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

				ClientHandler manejador = new ClientHandler(this.socket, this.input, this.output, nombre);

				// Crear hilo para el cliente
				Thread hilo = new Thread(manejador);

				// Añadir hilo del cliente al servidor para ser manipulado
				clientes.add(manejador);

				// Empezar el hilo del cliente
				hilo.start();
			}

		} catch (SocketException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws BindException {
		Server server = new Server(2727);
	}
}
