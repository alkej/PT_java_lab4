package pl.edu.pg.controllers;

import pl.edu.pg.model.Film;
import pl.edu.pg.services.FilmsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;


/**
 * Kontroler zawierający akcje związane z filmami w sklepie.
 *
 * Parametr "/films" w adnotacji @RequestMapping określa prefix dla adresów wszystkich akcji kontrolera.
 */
@RestController
@RequestMapping("/films")
public class FilmsController {

    //Komponent realizujący logikę biznesową operacji na filmach
    final FilmsService FilmsService;

    //Instancja klasy FilmsService zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public FilmsController(FilmsService FilmsService) {
        this.FilmsService = FilmsService;
    }

    /**
     * Pobieranie listy wszystkich filmów.
     *
     * Żądanie:
     * GET /films
     *
     * @return lista filmów
     */
    @GetMapping
    public List<Film> listFilms() {
        return FilmsService.findAll();
    }

    /**
     * Dodawanie nowego filmu.
     *
     * Żądanie:
     * POST /films
     *
     * @param Film obiekt zawierający dane nowego filmu, zostanie zbudowany na podstawie danych
     *             przesłanych w ciele żądania (automatyczne mapowanie z formatu JSON na obiekt
     *             klasy Film)
     * @param uriBuilder pomocniczy obiekt do budowania adresu wskazującego na nowo dodany film,
     *                   zostanie wstrzyknięty przez framework Spring
     *
     * @return odpowiedź HTTP dla klienta
     */
    @PostMapping
    public ResponseEntity<Void> addFilm(@RequestBody Film Film, UriComponentsBuilder uriBuilder) {

        if (FilmsService.find(Film.getId()) == null) {
            //Identyfikator nie istnieje w bazie danych - nowy film zostaje zapisany
            FilmsService.save(Film);

            //Jeśli zapisywanie się powiodło zwracana jest odpowiedź 201 Created z nagłówkiem Location, który zawiera
            //adres nowo dodanego filmu
            URI location = uriBuilder.path("/films/{id}").buildAndExpand(Film.getId()).toUri();
            return ResponseEntity.created(location).build();

        } else {
            //Identyfikator filmu już istnieje w bazie danych. Żądanie POST służy do dodawania nowych elementów,
            //więc zwracana jest odpowiedź z kodem błędu 409 Conflict
            return ResponseEntity.status(CONFLICT).build();
        }
    }

    /**
     * Pobieranie informacji o pojedynczym filmie
     *
     * Żądanie:
     * GET /films/{id}
     *
     * @param id identyfikator filmu
     *
     * @return odpowiedź 200 zawierająca dane filmu lub odpowiedź 404, jeśli film o podanym identyfikatorze nie
     * istnieje w bazie danych
     */
    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable UUID id) {
        //wyszukanie film w bazie danych
        Film Film = FilmsService.find(id);

        //W warstwie biznesowej brak filmu o podanym id jest sygnalizowany wartością null. Jeśli film nie został
        //znaleziony zwracana jest odpowiedź 404 Not Found. W przeciwnym razie klient otrzymuje odpowiedź 200 OK
        //zawierającą dane filmu w domyślnym formacie JSON
        return Film != null ? ResponseEntity.ok(Film) : ResponseEntity.notFound().build();
    }

    /**
     * Aktualizacja danych filmu.
     *
     * Żądanie:
     * PUT /films/{id}
     *
     * @param film
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFilm(@RequestBody Film film) {
        if (FilmsService.find(film.getId()) != null) {
            //aktualizacja danych jest możliwa o ile film o podanym id istnieje w bazie danych
            FilmsService.save(film);
            return ResponseEntity.ok().build();

        } else {
            //nie odnaleziono filmu o podanym id - odpowiedź 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

}
