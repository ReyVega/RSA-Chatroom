import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

	// Variables globales
	private Socket socket;
	private DataInputStream input;
	private DataOutputStream output;
	private Thread enviarMensaje, recibirMensaje;

	public Client(String address, int port) {
		try {
			// Realizar la conexión con el socket
			this.socket = new Socket(address, port);
			System.out.println("¡Bienvenido al chat!");

			// Recibir mensajes del servidor
			this.input = new DataInputStream(this.socket.getInputStream());

			// Mandar mensajes al servidor
			this.output = new DataOutputStream(this.socket.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			// Hilo que permite enviar mensajes
			this.enviarMensaje = new Thread(new Runnable() {
				public void run() {
					String nombre = "";
					while (true) {
						try {
							if (nombre.isBlank()) {
								System.out.print("> Ingresa tu nombre: ");
								nombre = br.readLine();
								output.writeUTF(nombre);
							} else {
								System.out.print("> ");
								String mensaje = br.readLine();
								if (mensaje.equalsIgnoreCase("salir")) {
									socket.close();
									input.close();
									output.close();
									System.exit(0);
								}
								output.writeUTF(mensaje);
							}
						} catch (IOException e) {
						}
					}
				}
			});

			// Hilo que permite recibir mensajes
			this.recibirMensaje = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							String mensaje = input.readUTF();
							System.out.println(mensaje);
							System.out.print("> ");
						} catch (SocketException e) {							
							System.exit(0);
						} catch (IOException e) {
						}
					}
				}
			});

			this.enviarMensaje.start();
			this.recibirMensaje.start();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		Client client = new Client("127.0.0.1", 2727);
	}
}
