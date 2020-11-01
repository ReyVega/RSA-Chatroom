import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {

	// Variables globales
	private Socket cliente;
	private DataInputStream input;
	private DataOutputStream output;
	private String nombre;

	// Constructor
	public ClientHandler(Socket client, DataInputStream input, DataOutputStream output, String nombre) {
		this.cliente = client;
		this.input = input;
		this.output = output;
		this.nombre = nombre;
		// Mostrarle a todos los clientes quien se conecto
		try {
			for (ClientHandler ch : Server.clientes) {
				if (!ch.equals(this)) {
					ch.output.writeUTF(this.nombre + " conectado");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// Mensaje recibido de cualquier cliente
		String recibido = "";
		try {
			while (true) {
				recibido = this.input.readUTF();
				if (recibido.contains("#")) {
					StringTokenizer st = new StringTokenizer(recibido, "#");
					String mensajePrivado = st.nextToken();
					String persona = st.nextToken();
					
					for (ClientHandler ch : Server.clientes) {
						if (ch.nombre.equals(persona)) {
							ch.output.writeUTF(this.nombre + ": " + mensajePrivado);
						}
					}
				} else {
					System.out.println("> " + this.nombre + ": " + recibido);

					// Mandar mensaje del cliente a todos menos a él mismo
					for (ClientHandler ch : Server.clientes) {
						if (!ch.equals(this)) {
							ch.output.writeUTF(this.nombre + ": " + recibido);
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
}
