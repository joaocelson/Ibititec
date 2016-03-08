package com.ibititec.ldapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.DateTimeKeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ibititec.ldapp.models.Comerciante;
import com.ibititec.ldapp.models.Endereco;
import com.ibititec.ldapp.models.Telefone;

import java.security.Permission;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DetalheActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 12;
    private Comerciante comerciante;
    private TextView txtNomeComerciante;
    private String telefoneChamar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        lerIntent();
        exibirMsgAtualizacao("Tela de detalhes aberta, comerciante: " + comerciante.getNome());
        txtNomeComerciante = (TextView) findViewById(R.id.txtNomeComercianteDetalhe);
        txtNomeComerciante.setText(comerciante.getNome());
        carregarTelefoneEndereco();
        setupFab();
    }

    private void carregarTelefoneEndereco() {
        Uri imageUri = Uri.parse(comerciante.getNomeFoto());
        SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.img_fresco_detalhe);
        draweeView.setImageURI(imageUri);

        LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_detalhe);
        TextView tx = new TextView(this);
        tx.setText("Telefone: ");
        layout.addView(tx);

        for (Telefone telefone : comerciante.getTelefones()) {
            TextView txTelefone = new TextView(this);
            txTelefone.setText(telefone.getNumero());
            layout.addView(txTelefone);
        }

        TextView txEndereco = new TextView(this);
        txEndereco.setText("Endereço: ");
        layout.addView(txEndereco);

        if (comerciante.getTelefones() != null) {
            telefoneChamar = comerciante.getTelefones().get(0).getNumero();
        }

        for (Endereco endereco : comerciante.getEnderecos()) {
            TextView txLogradouro = new TextView(this);
            txLogradouro.setText("Rua/Av:" + endereco.getLogradouro());
            layout.addView(txLogradouro);

            TextView txEnderecoNumero = new TextView(this);
            txEnderecoNumero.setText("N°: " + endereco.getNumero());
            layout.addView(txEnderecoNumero);

            TextView txEnderecoBairro = new TextView(this);
            txEnderecoBairro.setText("Bairro: " + endereco.getBairro());
            layout.addView(txEnderecoBairro);

            TextView txComplemento = new TextView(this);
            txComplemento.setText("Complemento: " + endereco.getComplemento());
            layout.addView(txComplemento);
        }
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(DetalheActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(DetalheActivity.this, Manifest.permission.CALL_PHONE)) {
                            showMessageOKCancel("Você precisa permitir acesso ao discador do telefone!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(DetalheActivity.this,new String[] {Manifest.permission.CALL_PHONE},
                                                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
                                        }
                                    });
                            return;
                        }
                    ActivityCompat.requestPermissions(DetalheActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + telefoneChamar));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DetalheActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Map<String, Integer> perms = new HashMap<String, Integer>();
                    // Inicial
                    perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void lerIntent() {
        Intent intent = getIntent();
        comerciante = (Comerciante) intent.getSerializableExtra("comerciante");
    }

    private void exibirMsgAtualizacao(String mensagem) {
        // Snackbar.make(findViewById(R.id.fab), String.format("%d Dados atualizados.", patios.size()), Snackbar.LENGTH_SHORT).show();
        Snackbar.make(findViewById(R.id.fab), mensagem, Snackbar.LENGTH_SHORT).show();
    }
}
