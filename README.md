# Rozetka Scraper

A small Java program that opens the "Mobile phones" category on rozetka.com.ua, scrolls the page until all products are loaded, and prints the name and price of every device. It also prints the devices labeled "Top sales".

Built with Java, Selenide and Chrome.

## Requirements

- JDK 11 or newer
- Google Chrome (the latest version)
- Gradle, or IntelliJ IDEA (which includes Gradle support)

## How to run

1. Clone the repository and open the project folder:

   ```bash
   git clone <your-repository-url>
   cd RozetkaScraper
   ```

2. Close all Chrome windows that were started by this program earlier (the program uses its own Chrome profile and cannot start if that profile is already open).

3. Run the program:

   - **From IntelliJ IDEA:** open the folder as a Gradle project and run the `main` method in `RozetkaScraper.java`.
   - **From the command line:**

     ```bash
     gradle run
     ```

A Chrome window will open. Do not close it: the program works in that window and closes it when it finishes.

## Good to know

- **Human verification:** if the site shows a "Confirm you are human" check, solve it manually in the opened window. The program waits up to 2 minutes and then continues on its own.
- **Saved profile:** Chrome data is stored in `~/rozetka-chrome-profile`. After you pass the check once, it usually appears less often. Delete this folder to start from scratch.
- **Different category:** change the `URL` constant in `RozetkaScraper.java`.
- **Output:** results are printed to the console.

## Troubleshooting

- **`List size mismatch` / no products found:** the site markup may have changed. Open the screenshot and HTML saved in `build/reports/tests/` and check that the `rz-product-tile` tag still exists.
- **Check keeps appearing:** use a Ukrainian IP address, do not run the program many times in a row, and pass the check manually once.
