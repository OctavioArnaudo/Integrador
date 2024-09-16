package com.example.app.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.app.data.model.request.PasswordCredentials;
import com.example.app.data.model.request.UserCredentials;
import com.example.app.data.model.response.PasswordResponse;
import com.example.app.data.model.response.UserResponse;
import com.example.app.util.HashUtility;

import java.util.ArrayList;
import java.util.List;

public class DbManager {
	private static final String TAG = "DbManager";
    public static final String TB_PASSWORDS = "passwords";
    public static final String TB_USERS = "users";
    private static final String ID = "id";
    public static final String PASSWORD_USERNAME = "username";
    public static final String PASSWORD_URL = "url";
    public static final String PASSWORD_KEYWORD = "keyword";
    public static final String PASSWORD_DESCRIPTION = "description";
    public static final String PASSWORD_USER = "user_id";
    public static final String PASSWORD_NAME = "name";

    public static final String CREATE_PASSWORD_TABLE = "CREATE TABLE IF NOT EXISTS " + TB_PASSWORDS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PASSWORD_USERNAME + " TEXT, " + PASSWORD_URL + " TEXT, " + PASSWORD_KEYWORD + " TEXT NOT NULL, " + PASSWORD_DESCRIPTION + " TEXT, " + PASSWORD_NAME + " TEXT, " + PASSWORD_USER + " INTEGER, " + "created_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')), " + "updated_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')), " + "FOREIGN KEY (" + PASSWORD_USER + ") REFERENCES "+ TB_USERS + "(" + ID + ") ON DELETE CASCADE" + ")";

    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String SALT = "salt";
    public static final String BIO = "biometric";

    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TB_USERS + " ( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EMAIL + " TEXT UNIQUE, " + PASSWORD + " TEXT, " + SALT + " TEXT, " + BIO + " INTEGER DEFAULT 0," + "created_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')), " + "updated_at TEXT DEFAULT (strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime')) " + ")";

    private final DbConnection connection;
    private static SQLiteDatabase db;
    private final SharedPreferences sharedPreferences;

    public DbManager(Context context) {
        this.connection = new DbConnection(context);
        this.sharedPreferences = context.getSharedPreferences("Storage", Context.MODE_PRIVATE);
    }

    public void open() throws SQLException {
        db = connection.getWritableDatabase();
    }

    public void close() {
        connection.close();
    }

    public boolean userRegister(UserCredentials user, int bio) throws HashUtility.HashingException, HashUtility.SaltException {
        try {
            Log.i(TAG, "LLEGA PARAMETRO DE BIO: " + bio);
            String salt = HashUtility.generateSalt();
            String hashedPassword = HashUtility.hashPassword(user.getPassword(), salt);

            ContentValues content = new ContentValues();
            content.put(EMAIL, user.getEmail());
            content.put(PASSWORD, hashedPassword);
            content.put(SALT, salt);
            content.put(BIO, bio);

            long newRowId = db.insertWithOnConflict(TB_USERS, null, content, SQLiteDatabase.CONFLICT_IGNORE);

            if (newRowId != -1 && bio != 0) {
                saveStorage(-1, bio);
            }

            return newRowId != -1;
        } catch (HashUtility.SaltException e) {
            Log.e("Error", "Salt generation error: " + e.getMessage());
            throw e;
        } catch (HashUtility.HashingException e) {
            Log.e("Error", "Hashing password error: " + e.getMessage());
            throw e;
        }
    }

    public String getSaltById(int userId) {
        String salt = null;
        Cursor cursor = null;

        try {
            String query = "SELECT " + SALT + " FROM " + TB_USERS + " WHERE " + ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

            int saltIndex = cursor.getColumnIndex(SALT);
            if (saltIndex != -1 && cursor.moveToFirst()) {
                salt = cursor.getString(saltIndex);
            } else {
                Log.e("Error", "No se pudo encontrar la columna '" + SALT + "'");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error de SQL al registrar la contraseña: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return salt;
    }

    public boolean passwordRegister(PasswordCredentials psw) throws Exception {

        if (psw == null) {
            throw new IllegalArgumentException("PasswordCredentials no puede ser null");
        }
        Log.i(TAG, "Llega al passwordRegister: " + psw.getUsername());
        try {
            String salt = getSaltById(psw.getUserId());
            if (salt != null) {

                ContentValues content = new ContentValues();
                content.put(PASSWORD_USERNAME, psw.getUsername());
                content.put(PASSWORD_URL, psw.getUrl());
                String hashedPassword = HashUtility.encrypt(psw.getKeyword(), salt);
                content.put(PASSWORD_KEYWORD, hashedPassword); // Guardar la contraseña hasheada
                content.put(PASSWORD_DESCRIPTION, psw.getDescription());
                content.put(PASSWORD_NAME, psw.getName());
                content.put(PASSWORD_USER, psw.getUserId());

                long newRowId = db.insertWithOnConflict(TB_PASSWORDS, null, content, SQLiteDatabase.CONFLICT_IGNORE);
                Log.i(TAG, "Se registra PWD: " + newRowId + " de nombre: " + content.getAsString(PASSWORD_NAME));
                return newRowId != -1;
            } else {
                Log.e("Error", "No existe el usuario");
                return false;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error de SQL al registrar la contraseña: " + e.getMessage());
            throw e;
        } catch (HashUtility.HashingException e) {
            Log.e(TAG, "Error al hashear la contraseña: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Password registration error: " + e.getMessage());
            throw e;
        } finally {
            db.close();
        }
    }

    public boolean validateUser(String pwd, String email) throws HashUtility.HashingException {
        UserResponse user = this.getUserByEmail(email);

        if (user != null && HashUtility.checkPassword(pwd, user.getPassword(), user.getSalt())) {
            saveStorage(user.getId(), user.getBiometric());
            Log.i(TAG, "userID guardado en DbManayer: " + sharedPreferences.getInt("userId", -1));
            Log.i(TAG, "biometric guardado en DbManayer: " + sharedPreferences.getInt(BIO, -1));
            return true; // Las credenciales son válidas
        }
        return false; // Las credenciales son inválidas
    }

    private UserResponse getUserByEmail(String email) {
        UserResponse user = null;
        Log.i(TAG, "llega el email: " + email);
        try {
            String query = "SELECT * FROM " + TB_USERS + " WHERE " + EMAIL + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{email});
            int idIndex = cursor.getColumnIndex(ID);
            int emailIndex = cursor.getColumnIndex(EMAIL);
            int pwdIndex = cursor.getColumnIndex(PASSWORD);
            int salIndex = cursor.getColumnIndex(SALT);
            int bioIndex = cursor.getColumnIndex(BIO);

            if (emailIndex != -1 && cursor.moveToFirst()) {
                int id = cursor.getInt(idIndex);
                String userEmail = cursor.getString(emailIndex);
                String password = cursor.getString(pwdIndex);
                String sal = cursor.getString(salIndex);
                int biometric = cursor.getInt(bioIndex);

                user = new UserResponse(id, userEmail, password, sal, biometric);
            }
            cursor.close();
            db.close();

        } catch (Exception e) {
            Log.e(TAG, "Error al obtener el usuario por " + EMAIL, e);
        }

        return user;
    }

    public Cursor getPasswordsForUser(int userId) {
        try {
            String query = "SELECT * FROM " + TB_PASSWORDS + " WHERE " + PASSWORD_USER + " = ? ORDER BY " + PASSWORD_NAME;
            return db.rawQuery(query, new String[]{String.valueOf(userId)});
        } catch (SQLException e) {
            Log.e("Error", "Error de SQL: " + e.getMessage());
            throw e;
        }
    }

    public boolean userWhitBiometrics() {
        try {
            this.open();
            String query = "SELECT * FROM " + TB_USERS + " WHERE " + BIO + " = 1";
            Cursor cursor = db.rawQuery(query, null);
            int emailIndex = cursor.getColumnIndex(EMAIL);
            int idIndex = cursor.getColumnIndex(ID);

            if (emailIndex != -1 && cursor.moveToFirst()) {
                Log.i(TAG, "Usuario con biometria: " + cursor.getString(emailIndex));
                saveStorage(cursor.getInt(idIndex), 1);
                cursor.close();
                return true;
            }

        } catch (SQLException e) {
            Log.e("Error", "Error de SQL: " + e.getMessage());
        } finally {
            this.close();
        }
        return false;
    }

    private void saveStorage(int userId, int biometricValue) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (userId != -1) {
            editor.putInt("userId", userId);
        }
        editor.putInt("biometric", biometricValue);
        editor.apply();
    }

    public List<PasswordResponse> getPasswordsListForUserId(int userId) {
        List<PasswordResponse> passwords = null;
        Cursor cursor = null;
        this.open();

        try {
            passwords = new ArrayList<>();
            cursor = getPasswordsForUser(userId);
            int idIndex = cursor.getColumnIndex(ID);
            int usernameIndex = cursor.getColumnIndex(PASSWORD_USERNAME);
            int urlIndex = cursor.getColumnIndex(PASSWORD_URL);
            int keywordIndex = cursor.getColumnIndex(PASSWORD_KEYWORD);
            int descriptionIndex = cursor.getColumnIndex(PASSWORD_DESCRIPTION);
            int nameIndex = cursor.getColumnIndex(PASSWORD_NAME);


            if (idIndex != -1) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(idIndex);
                    String username = cursor.getString(usernameIndex);
                    String url = cursor.getString(urlIndex);
                    String keyword = cursor.getString(keywordIndex);
                    String description = cursor.getString(descriptionIndex);
                    String name = cursor.getString(nameIndex);

                    PasswordResponse passwordResponse = new PasswordResponse(id, username, url, keyword, description, name);
                    passwords.add(passwordResponse);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al crear lista de contraseñas", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            this.close();
        }

        return passwords;
    }

    public void deletePassword(int passwordId) {
        try {
            this.open();
            db.delete(TB_PASSWORDS, ID + " = ?", new String[]{String.valueOf(passwordId)});
        } catch (SQLException e) {
            Log.e("Error", "Error al eliminar la contraseña: " + e.getMessage());
        } finally {
            this.close();
        }
    }

    public PasswordResponse getPasswordDetails(int passwordId, int userId) {
        this.open();
        PasswordResponse passwordResponse = null;
        Cursor cursor = null;

        Log.i(TAG, "llega el id de password: "+passwordId);


        try {
            String query = "SELECT * FROM " + TB_PASSWORDS + " WHERE " + ID + " = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(passwordId)});

            int nameIndex = cursor.getColumnIndex(PASSWORD_NAME);
            int usernameIndex = cursor.getColumnIndex(PASSWORD_USERNAME);
            int keywordIndex = cursor.getColumnIndex(PASSWORD_KEYWORD);
            int urlIndex = cursor.getColumnIndex(PASSWORD_URL);
            int descriptionIndex = cursor.getColumnIndex(PASSWORD_DESCRIPTION);
            String salt = this.getSaltById(userId);

            if (cursor.moveToFirst()) {
                String name = cursor.getString(nameIndex);
                String username = cursor.getString(usernameIndex);
                String keyword = HashUtility.decrypt(cursor.getString(keywordIndex),salt);
                String url = cursor.getString(urlIndex);
                String description = cursor.getString(descriptionIndex);
                Log.d(TAG, "Name: " + name);
                Log.d(TAG, "Username: " + username);
                Log.d(TAG, "Keyword: " + keyword);
                Log.d(TAG, "URL: " + url);
                Log.d(TAG, "Description: " + description);

                passwordResponse = new PasswordResponse(passwordId, username, url, keyword, description, name);
            }
        } catch (SQLException e) {
            Log.e("Error", "Error al obtener detalles de la contraseña: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "ERROR: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            this.close();
        }

        return passwordResponse;
    }

    public boolean updatePassword(int passwordId, PasswordCredentials updatedPassword, int userId) {
        this.open();
        try {
            ContentValues content = new ContentValues();
            content.put(PASSWORD_NAME, updatedPassword.getName());
            content.put(PASSWORD_USERNAME, updatedPassword.getUsername());
            String salt = this.getSaltById(userId);
            String pass = HashUtility.encrypt(updatedPassword.getKeyword(),salt);
            content.put(PASSWORD_KEYWORD, pass);
            content.put(PASSWORD_URL, updatedPassword.getUrl());
            content.put(PASSWORD_DESCRIPTION, updatedPassword.getDescription());

            String whereClause = ID + " = ?";
            String[] whereArgs = {String.valueOf(passwordId)};

            int rowsAffected = db.update(TB_PASSWORDS, content, whereClause, whereArgs);

            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e("Error", "Error al actualizar la contraseña: " + e.getMessage());
            return false;
        }catch (Exception e){
            Log.e(TAG, "Error: "+e.getMessage());
            return false;
        } finally {
            this.close();
        }

    }
    
   // Nueva función para verificar si ya existe una contraseña con el mismo nombre
    public static boolean passwordExistsByName(String name) {
        Cursor cursor = null;
        boolean exists = false;

        try {
            String query = "SELECT COUNT(*) FROM " + TB_PASSWORDS + " WHERE " + PASSWORD_NAME + " = ?";
            cursor = db.rawQuery(query, new String[]{name});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                exists = count > 0;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error al verificar si existe la contraseña con el nombre: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return exists;
    }

}
