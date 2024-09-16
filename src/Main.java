import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Main extends JFrame {

    private String[] datos;
    private String tipoGrafica;

    public Main() {
        // Configuramos la ventana principal
        setTitle("Gráficas de Datos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana

        // Fondo de la ventana (opcional, ajusta la ruta a tu imagen)
        setContentPane(new JLabel(new ImageIcon("ruta/de/tu/imagen/fondo.jpg")));
        setLayout(new BorderLayout());

        // Inicializamos la interfaz gráfica
        initUI();
    }

    private void initUI() {
        // Panel para los controles
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new FlowLayout());

        // ComboBox para seleccionar el tipo de gráfica
        String[] opcionesGraficas = {"Barras", "Pastel"};
        JComboBox<String> comboBox = new JComboBox<>(opcionesGraficas);
        panelControles.add(comboBox);

        // Botón para capturar los datos
        JButton btnCapturarDatos = new JButton("Capturar Datos");
        panelControles.add(btnCapturarDatos);

        // Botón para graficar
        JButton btnGraficar = new JButton("Graficar");
        panelControles.add(btnGraficar);

        // Agregar panel de controles al JFrame
        add(panelControles, BorderLayout.NORTH);

        // Acción para capturar datos
        btnCapturarDatos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                datos = capturarDatos(); // Captura de 3 a 5 registros
            }
        });

        // Acción para elegir el tipo de gráfica
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tipoGrafica = (String) comboBox.getSelectedItem(); // "Barras" o "Pastel"
            }
        });

        // Acción para graficar
        btnGraficar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (datos != null && tipoGrafica != null) {
                    repaint(); // Redibujar la gráfica con los datos seleccionados
                } else {
                    JOptionPane.showMessageDialog(null, "Captura datos y selecciona un tipo de gráfica.");
                }
            }
        });
    }

    // Método para capturar los datos de entrada
    private String[] capturarDatos() {
        int registros = 3; // Puedes ajustarlo a un valor entre 3 y 5
        String[] items = new String[registros];
        for (int i = 0; i < registros; i++) {
            String nombreItem = JOptionPane.showInputDialog(null, "Ingrese el nombre del ítem " + (i + 1));
            String valorItem = JOptionPane.showInputDialog(null, "Ingrese el valor para " + nombreItem);
            items[i] = nombreItem + "," + valorItem;
        }
        return items;
    }

    // Sobreescribir el método paint para dibujar las gráficas
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (datos != null && tipoGrafica != null) {
            if ("Barras".equals(tipoGrafica)) {
                dibujarBarras(g, datos);
            } else if ("Pastel".equals(tipoGrafica)) {
                dibujarPastel(g, datos);
            }
        }
    }

    // Método para dibujar la gráfica de barras
    private void dibujarBarras(Graphics g, String[] items) {
        Graphics2D g2d = (Graphics2D) g;
        int xPos = 100;
        int maxValor = 0;

        // Calcular el valor máximo para escalar las barras
        for (String item : items) {
            int valor = Integer.parseInt(item.split(",")[1]);
            if (valor > maxValor) {
                maxValor = valor;
            }
        }

        for (String item : items) {
            String[] datos = item.split(",");
            String nombre = datos[0];
            int valor = Integer.parseInt(datos[1]);

            // Dibujar barra
            g2d.setPaint(new TexturePaint(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB), new Rectangle(0, 0, 10, 10)));
            int alturaBarra = (valor * 300) / maxValor; // Escalar la altura de las barras
            g2d.fillRect(xPos, 500 - alturaBarra, 50, alturaBarra);

            // Dibujar nombre del ítem
            g2d.setPaint(Color.BLACK);
            g2d.drawString(nombre, xPos, 520);

            xPos += 100; // Mover la posición para la siguiente barra
        }
    }

    // Método para dibujar la gráfica de pastel
    private void dibujarPastel(Graphics g, String[] items) {
        Graphics2D g2d = (Graphics2D) g;
        int total = 0;

        // Calcular el total de los valores
        for (String item : items) {
            total += Integer.parseInt(item.split(",")[1]);
        }

        int inicioAngulo = 0;
        int x = 200, y = 150, width = 300, height = 300;

        // Colores y degradados para cada rebanada
        Color[] colores = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA};

        for (int i = 0; i < items.length; i++) {
            String[] datos = items[i].split(",");
            String nombre = datos[0];
            int valor = Integer.parseInt(datos[1]);
            int angulo = (int) Math.round((double) valor / total * 360);

            // Dibujar rebanada con degradado personalizado
            GradientPaint degradado = new GradientPaint(x, y, colores[i], x + width, y + height, colores[i].brighter(), true);
            g2d.setPaint(degradado);
            g2d.fillArc(x, y, width, height, inicioAngulo, angulo);

            // Calcular la posición del texto (nombre y porcentaje) en la rebanada
            int mitadAngulo = inicioAngulo + angulo / 2;
            double radianes = Math.toRadians(mitadAngulo);
            int textoX = (int) (x + width / 2 + Math.cos(radianes) * 150);
            int textoY = (int) (y + height / 2 - Math.sin(radianes) * 150);

            // Mostrar nombre y porcentaje
            g2d.setPaint(Color.BLACK);
            String porcentaje = String.format("%.1f%%", (double) valor / total * 100);
            g2d.drawString(nombre + " (" + porcentaje + ")", textoX, textoY);

            // Dibujar la figura cuadrada con el degradado
            g2d.setPaint(degradado);
            g2d.fillRect(x + 350, y + 50 + i * 60, 50, 50);
            g2d.setPaint(Color.BLACK);
            g2d.drawString(nombre, x + 410, y + 80 + i * 60);

            inicioAngulo += angulo; // Actualizar el inicio para la siguiente rebanada
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
