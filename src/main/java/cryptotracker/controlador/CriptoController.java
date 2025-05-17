package cryptotracker.controlador;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/cripto")
@CrossOrigin(origins = "http://localhost:4200")
public class CriptoController {

    @GetMapping("/precios")
    public ResponseEntity<?> obtenerPreciosCriptos() {
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=100&convert=USD";
        String apiKey = "ea397464-a56e-442d-8ad2-74e9faeff22d"; 

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CMC_PRO_API_KEY", apiKey);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener datos de criptomonedas");
        }
    }
}