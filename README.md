# PackUp – Specyfikacja wymagań

**Wersja:** 1.0  
**Data utworzenia:** ...  
**Data ostatniej modyfikacji:** ...  
**Autorzy:** ...

---

## 1. Ogólne informacje

### 1.1 Opis systemu
PackUp to mobilna aplikacja na Android do organizacji wydarzeń i wspólnych wyjazdów w grupach znajomych. Umożliwia łatwe zarządzanie terminami, wspólnymi zadaniami, zakupami, ogłoszeniami oraz podziałem kosztów. Intuicyjny interfejs i funkcje dopasowane do nieformalnych grup użytkowników (np. studentów, znajomych z różnych miast) pozwalają na szybką komunikację i sprawne planowanie.

### 1.2 Odbiorcy docelowi
- Grupy znajomych planujące wspólne wydarzenia  
- Studenci z różnych kierunków lub uczelni, którym trudno znaleźć wspólny termin  
- Młode osoby pracujące, które chcą utrzymać kontakt i wspólnie organizować czas wolny  

### 1.3 Oczekiwane korzyści biznesowe
- Zwiększenie zaangażowania użytkowników w organizację wspólnych wydarzeń  
- Ułatwienie komunikacji i planowania w nieformalnych grupach  
- Zmniejszenie liczby pomyłek, nieporozumień i zgubionych informacji  
- Oszczędność czasu dzięki centralizacji zadań, ankiet i ustaleń  

---

## 2. Procesy biznesowe

### BP01: Organizacja wspólnego wydarzenia
Organizator tworzy nową grupę (np. „Majówka 2025”) i zaprasza uczestników. Każdy zaznacza dostępność w kalendarzu. System proponuje najlepsze terminy. Uczestnicy głosują lub proponują inne. Organizator może tworzyć ankiety i ogłoszenia.

### BP02: Zarządzanie listą zadań i zakupów
Uczestnik dodaje zadanie (np. „zorganizować grill”) i przypisuje je sobie lub innym. Można odznaczyć wykonanie. Historia zmian jest zapisywana.

### BP03: Rozliczanie wydatków
Uczestnik dodaje wydatek z opisem, kwotą i osobami, których dotyczy. System przelicza saldo i pokazuje kto, ile i komu. Możliwe oznaczenie długu jako spłaconego.

---

## 3. Aktorzy

- **Uczestnik**  
  Dołącza do grup, korzysta z kalendarza, list, ogłoszeń, ankiet, wydatków, edytuje swoje dane.

- **Organizator**  
  Tworzy grupy, zarządza ogłoszeniami, przypina ważne informacje, pełni rolę administratora.

---

## 4. Obiekty biznesowe

### Grupa
- Nazwa  
- Opis  
- Lista uczestników  
- Ogłoszenia  
- Lista zadań/zakupów  
- Wydatki  
- Kalendarz dostępności  

### Użytkownik
- Nazwa użytkownika  
- Adres e-mail  
- Numer telefonu  
- Rola w grupie  

### Zadanie/Zakup
- Opis  
- Status (otwarte / zrobione)  
- Osoba przypisana  
- Data dodania / wykonania  

### Wydatek
- Kwota  
- Opis  
- Osoba płacąca  
- Lista osób, których dotyczy  
- Status rozliczenia (otwarty / spłacony)  

### Ankieta
- Pytanie  
- Lista opcji  
- Głosy użytkowników  
- Data zakończenia  

---

## 5. Diagram kontekstu
*Graficzny diagram kontekstu – do dodania jako obrazek.*

---

## 6. Wymagania funkcjonalne

### 6.1 Moduły

| Symbol | Moduł               | Opis                                                  |
|--------|---------------------|--------------------------------------------------------|
| G      | Grupy i wydarzenia  | Tworzenie i zarządzanie wydarzeniami/grupami          |
| K      | Kalendarz           | Zbieranie dostępności, wspólne terminy                |
| L      | Listy               | Zadania, zakupy, przypisywanie zadań                  |
| A      | Ankiety i głosowania| Tworzenie ankiet i zarządzanie głosami                |
| O      | Ogłoszenia          | Tworzenie ogłoszeń dla grupy                          |
| W      | Wydatki             | Wprowadzanie i rozliczanie wydatków                   |
| U      | Użytkownicy         | Rejestracja, logowanie, zarządzanie kontem            |

### 6.2 Opowieści użytkownika

- **G01. Tworzenie grupy**  
  _Jako użytkownik chcę stworzyć grupę, aby zaprosić znajomych i wspólnie coś zaplanować._

- **K01. Zbieranie dostępności**  
  _Jako uczestnik chcę zaznaczyć dostępne dni, aby system znalazł najlepszy termin._

- **K02. Wyświetlanie wspólnych terminów**  
  _Jako uczestnik chcę zobaczyć najlepsze terminy pasujące większości._

- **K03. Wyznaczanie terminu**  
  _Jako organizator chcę wybrać termin spośród zaproponowanych lub ustalić go samodzielnie._

- **L01. Tworzenie listy zadań**  
  _Jako organizator chcę stworzyć listę zadań lub zakupów._

- **L02. Dodawanie zadań**  
  _Jako uczestnik chcę dodać i przypisać zadanie._

- **A01. Tworzenie ankiety**  
  _Jako organizator chcę stworzyć ankietę (np. wybór miejsca)._

- **O01. Tworzenie ogłoszenia**  
  _Jako uczestnik chcę dodać ogłoszenie._

- **O02. Przypinanie ogłoszeń**  
  _Jako organizator chcę przypinać ważne ogłoszenia._

- **W01. Dodawanie wydatku**  
  _Jako uczestnik chcę dodać wspólny wydatek._

- **W02. Przegląd salda**  
  _Jako uczestnik chcę zobaczyć aktualne rozliczenia._

- **U01. Rejestracja i logowanie**  
  _Jako nowy użytkownik chcę się zarejestrować i logować._

---

## 7. Wymagania pozafunkcjonalne

- **NFR01:** System działa poprawnie na ekranach mobilnych o szerokości min. 600 px.  
- **NFR02:** System zgodny z RODO (pseudonimizacja danych osobowych).  
- **NFR03:** Architektura oparta o REST API – gotowość na wielu klientów.  
- **NFR04:** System wspiera internacjonalizację – różne języki interfejsu.  
- **NFR05:** Adres e-mail jako unikalny identyfikator użytkownika.  
- **NFR06:** Kompatybilność z przeglądarkami:  
  - Google Chrome 72.0.36  
  - Mozilla Firefox 64.0.2  
  - Microsoft Edge 17.17134  
- **NFR07:** Hasła muszą mieć min. 8 znaków, co najmniej 1 wielką literę i 1 cyfrę.  

### Przykład szczegółowego wymagania czasowego:
- Średni czas odpowiedzi systemu ≤ 4 sekundy (normalne obciążenie)  
- Maksymalny czas odpowiedzi ≤ 15 sekund (przy maksymalnym obciążeniu)  
- Dla 100 jednoczesnych zapytań średni czas obsługi ≤ 6 sekund, maksymalny ≤ 20 sekund  
- Warunki testowe:  
  - 200 000 aktywnych ogłoszeń  
  - Serwer: 8-core CPU @ 2.4 GHz, 64 GB RAM, dysk NVMe/SAS min. 1.6 TB  
  - Łącze użytkownika ≥ 100 Mb/s  
  - Brak dodatkowych usług na serwerze  

---
