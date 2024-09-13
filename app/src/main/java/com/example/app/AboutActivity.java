package com.example.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import android.widget.VideoView;
// Importa WebView y WebViewClient para el código alternativo
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Configuración del botón que navega a ShowPasswordsActivity
        setupAboutButton();
        
        // Configuración del VideoView
        setupVideoView();

        // Configuración alternativa con WebView
        // setupWebView();
    }

    // Método para configurar el botón
    private void setupAboutButton() {
        MaterialButton btnAbout = findViewById(R.id.btn_about);
        btnAbout.setOnClickListener(view -> navigateToShowPasswordsActivity());
    }

    // Método para iniciar la ShowPasswordsActivity
    private void navigateToShowPasswordsActivity() {
        Intent intent = new Intent(AboutActivity.this, ShowPasswordsActivity.class);
        startActivity(intent);
    }

    // Método para configurar el VideoView
    private void setupVideoView() {
        VideoView videoView = findViewById(R.id.videoView);

        // Establece la URI del video desde la carpeta raw
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.videoView);
        videoView.setVideoURI(videoUri);

        // Opcional: configurar los controles de medios (play, pause, etc.)
        videoView.setMediaController(new android.widget.MediaController(this));

        // Opcional: iniciar la reproducción automáticamente
        videoView.start();
    }

    // Configuración alternativa con WebView
    /*
    private void setupWebView() {
        WebView webView = findViewById(R.id.webView);

        // Configura el WebView para permitir JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Asegúrate de que los enlaces se abran en el WebView y no en un navegador externo
        webView.setWebViewClient(new WebViewClient());

        // URL del video de YouTube
        String videoUrl = "https://www.youtube.com/embed/YOUR_VIDEO_ID"; // Reemplaza con el ID del video de YouTube

        // Carga el video en el WebView
        webView.loadUrl(videoUrl);
    }
    */
}
