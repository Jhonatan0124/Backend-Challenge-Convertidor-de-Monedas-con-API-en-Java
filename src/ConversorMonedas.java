import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ConversorMonedas {
    private static final String API_KEY = "e60f4e1956ed00d6b1ed063e";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/e60f4e1956ed00d6b1ed063e/latest/";

    private static final Map<String, String> monedasLatinoamerica = new HashMap<>();
    static {
        monedasLatinoamerica.put("ARS", "Peso Argentino");
        monedasLatinoamerica.put("BRL", "Real Brasileño\n");
        monedasLatinoamerica.put("CLP", "Peso Chileno");
        monedasLatinoamerica.put("COP", "Peso Colombiano");
        monedasLatinoamerica.put("MXN", "Peso Mexicano");
        monedasLatinoamerica.put("PEN", "Sol Peruano");
        monedasLatinoamerica.put("UYU", "Peso Uruguayo");
    }

    public double convertirAMoneda(double cantidad, String monedaOrigen, String monedaDestino) throws IOException {
        String url = BASE_URL + monedaOrigen;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNextLine()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        // Extraer la tasa de conversión de la respuesta JSON
        String jsonResponse = response.toString();
        double tasaConversion = obtenerTasa(jsonResponse, monedaDestino);

        return cantidad * tasaConversion;
    }

    private double obtenerTasa(String jsonResponse, String monedaDestino) {
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonObject conversionRates = jsonObject.getAsJsonObject("conversion_rates");
        double tasaConversion = conversionRates.get(monedaDestino).getAsDouble();
        return tasaConversion;
    }


    public static void main(String[] args) {
        ConversorMonedas conversor = new ConversorMonedas();
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n\n********** Bienvenido al Conversor de Monedas **********\n");
        System.out.println("Monedas de Latinoamérica disponibles:\n");

        for (Map.Entry<String, String> entry : monedasLatinoamerica.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }

        System.out.println("Ingrese la cantidad a convertir:");
        double cantidad = scanner.nextDouble();

        System.out.println("Ingrese la moneda de origen (por ejemplo, COP, BRL):");
        String monedaOrigen = scanner.next().toUpperCase();

        System.out.println("Ingrese la moneda de destino (por ejemplo, USD, EUR):");
        String monedaDestino = scanner.next().toUpperCase();

        scanner.close();

        try {
            double cantidadConvertida = conversor.convertirAMoneda(cantidad, monedaOrigen, monedaDestino);
            System.out.println("Cantidad convertida a " + monedaDestino + ": " + cantidadConvertida);
        } catch (IOException e) {
            System.err.println("Error al conectar con la API: " + e.getMessage());
        }
    }
}


