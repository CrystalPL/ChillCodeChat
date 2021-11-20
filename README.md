# Chat

Plugin dodający możliwość kontrolowania częstotliwości wysyłanych wiadomości dla poszczególnych graczy, grup i całego
serwera. Wtyczka oferuje także możliwość ustawienia minimalnej ilości wykopanego kamienia, aby pisać na chat'cie.
___

### Instalacja

Plugin przeznaczony jest dla wersji MC od 1.8 do 1.17.1. Działa z dowolnym silnikiem opartym o BukkitAPI — CraftBukkit,
Spigot, Paper, Tuinity itp.
___

### Konfiguracja

W pliku config.yml zna jdują się ustawienia do połączenia z bazą danych. Baza danych jest wymagana, by trzymać
informacje o ilości wykopanego kamienia oraz o opóźnieniu dla danego gracza. Przechowywać te dane można w: SQLite, MySQL
lub w MongoDB.

W ustawieniach znajduję się pole prefix, podaję się tam przedrostek, jaki ma występować w nazwach tabel (SQLite, MYSQL)
lub kolekcji (MongoDB).
___

### Komendy

W pluginie jest jedna komenda /chat, z aliasem /c. Komenda, jak i alias jest możliwa do zmiany w pliku konfiguracyjnym.
Przed użyciem komendy, gracz musi mieć uprawnienie **chillcode.chat.base**.

|                 Komenda               |          Uprawnienie        |                       Opis komendy                    |
|---------------------------------------|-----------------------------|-------------------------------------------------------|
|/chat on                               | chillcode.chat.status       | Włącza chat                                           |
|/chat off                              | chillcode.chat.status       | Wyłącza chat                                          |
|/chat clear                            | chillcode.chat.clear        | Czyści chat                                           |
|/chat clear \<gracz>                   | chillcode.chat.clear.player | Czyści chat podanemu graczowi                         |
|/chat slowmode player \<gracz> \<czas> | chillcode.chat.slowmode     | Ustawia opóźnienie w pisaniu dla gracza               |
|/chat slowmode server \<czas>          | chillcode.chat.slowmode     | Ustawia opóźnienie w pisaniu dla całego serwera       |
|/chat slowmode group \<grupa> \<czas>  | chillcode.chat.slowmode     | Ustawia opóźnienie w pisaniu dla danej grupy          |
|/chat slowmode info player \<gracz>    | chillcode.chat.slowmode     | Wyświetla informację o opóźnieniu dla podanego gracza |
|/chat slowmode info                    | chillcode.chat.slowmode     | Wyświetla informację o opóźnienie dla serwer i grup   |
|/chat stone \<ilość kamienia>          | chillcode.chat.stone        | Ustawia minimalną ilość wykopanego kamienia           |

___

### Dodatkowe uprawnienia

|           Uprawnienie          |                                    Opis uprawnienia                                   |
|--------------------------------|---------------------------------------------------------------------------------------|
| chillcode.chat.clear.bypass    | Jeżeli gracz ma to uprawnienie, jego chat nie zostanie wyczyszczony                   |
| chillcode.chat.stone.bypass    | Jeżeli gracz ma to uprawnienie, nie obowiązuje go minimalna ilość wykopanego kamienia |
| chillcode.chat.slowmode.bypass | Jeżeli gracz ma to uprawnienie, nie obowiązuje go opóźnienie w pisaniu na chat'cie    |