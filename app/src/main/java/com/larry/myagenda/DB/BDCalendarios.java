package com.larry.myagenda.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.database.sqlite.SQLiteDatabase;

public class BDCalendarios {

    // Constructor privado para evitar instancias de la clase
    private BDCalendarios() {
    }

    // Clase interna que define las columnas de la tabla de calendarios
    public static class CalendarioEntrada implements BaseColumns {
        public static final String TABLE_NAME = "calendarios";
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_ICONO_RUTA = "icono_ruta";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_USER_ID = "user_id";
    }

    // Clase interna que define las columnas de la tabla de tareas
    public static class TareaEntrada implements BaseColumns {
        public static final String TABLE_NAME = "Tareas";
        public static final String COLUMN_CALENDARIO_ID = "calendario_id";
        public static final String COLUMN_TITULO = "titulo";
        public static final String COLUMN_CONTENIDO = "contenido";
        public static final String COLUMN_FECHA = "fecha";
    }

    // Clase para ayudar a gestionar la base de datos de calendarios
    public static class CalendarioDBHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "calendarios.db";
        private static final int DATABASE_VERSION = 1;

        // Constructor que recibe el contexto y llama al constructor de la clase padre
        public CalendarioDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Método llamado cuando se crea la base de datos por primera vez
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Sentencia SQL para crear la tabla de calendarios
            final String SQL_CREATE_CAL_TABLE =
                    "CREATE TABLE " + CalendarioEntrada.TABLE_NAME + " (" +
                            CalendarioEntrada._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            CalendarioEntrada.COLUMN_NOMBRE + " TEXT," +
                            CalendarioEntrada.COLUMN_ICONO_RUTA + " TEXT," +
                            CalendarioEntrada.COLUMN_COLOR + " INTEGER," +
                            CalendarioEntrada.COLUMN_USER_ID + " TEXT)";

            // Sentencia SQL para crear la tabla de tareas
            final String SQL_CREATE_NOTA_TABLE =
                    "CREATE TABLE " + TareaEntrada.TABLE_NAME + " (" +
                            TareaEntrada._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            TareaEntrada.COLUMN_CALENDARIO_ID + " INTEGER," +
                            TareaEntrada.COLUMN_TITULO + " TEXT," +
                            TareaEntrada.COLUMN_CONTENIDO + " TEXT," +
                            TareaEntrada.COLUMN_FECHA + " TEXT," +
                            "FOREIGN KEY(" + TareaEntrada.COLUMN_CALENDARIO_ID + ") REFERENCES " +
                            CalendarioEntrada.TABLE_NAME + "(" + CalendarioEntrada._ID + "))";


            // Ejecutar las sentencias SQL para crear las tablas
            db.execSQL(SQL_CREATE_CAL_TABLE);
            db.execSQL(SQL_CREATE_NOTA_TABLE);
        }

        // Método llamado cuando se actualiza la versión de la base de datos
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Eliminar las tablas existentes si existen
            db.execSQL("DROP TABLE IF EXISTS " + CalendarioEntrada.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TareaEntrada.TABLE_NAME);

            // Crear las nuevas tablas
            onCreate(db);
        }

        // Método para insertar un nuevo calendario en la base de datos
        public long insertarCalendario(String userId, String nombreCalendario, String iconoRuta, int color) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CalendarioEntrada.COLUMN_NOMBRE, nombreCalendario);
            values.put(CalendarioEntrada.COLUMN_ICONO_RUTA, iconoRuta);
            values.put(CalendarioEntrada.COLUMN_COLOR, color);
            values.put(CalendarioEntrada.COLUMN_USER_ID, userId);

            long resultado = db.insert(CalendarioEntrada.TABLE_NAME, null, values);

            db.close();

            return resultado;
        }

        // Método para obtener los calendarios del usuario actual de la base de datos
        public static Cursor obtenerCalendariosDelUsuario(Context context, String userId) {
            CalendarioDBHelper dbHelper = new CalendarioDBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    CalendarioEntrada._ID,
                    CalendarioEntrada.COLUMN_NOMBRE,
                    CalendarioEntrada.COLUMN_ICONO_RUTA,
                    CalendarioEntrada.COLUMN_COLOR
            };

            String selection = CalendarioEntrada.COLUMN_USER_ID + " = ?";
            String[] selectionArgs = {userId};

            Cursor cursor = db.query(
                    CalendarioEntrada.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            return cursor;
        }

        // Método para insertar una nueva nota en la base de datos
        public static long insertarTarea(Context context, String idCalendario, String titulo, String contenido, String fecha) {
            CalendarioDBHelper dbHelper = new CalendarioDBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BDCalendarios.TareaEntrada.COLUMN_CALENDARIO_ID, idCalendario);
            values.put(BDCalendarios.TareaEntrada.COLUMN_TITULO, titulo);
            values.put(BDCalendarios.TareaEntrada.COLUMN_CONTENIDO, contenido);
            values.put(BDCalendarios.TareaEntrada.COLUMN_FECHA, fecha);

            long resultado = db.insert(BDCalendarios.TareaEntrada.TABLE_NAME, null, values);

            db.close();

            return resultado;
        }

        public static Cursor obtenerTareasDeCalendario(Context context, String idCalendario) {
            CalendarioDBHelper dbHelper = new CalendarioDBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    TareaEntrada._ID,
                    TareaEntrada.COLUMN_TITULO,
                    TareaEntrada.COLUMN_CONTENIDO,
                    TareaEntrada.COLUMN_FECHA
            };

            String selection = TareaEntrada.COLUMN_CALENDARIO_ID + " = ?";
            String[] selectionArgs = {String.valueOf(idCalendario)};

            Cursor cursor = db.query(
                    TareaEntrada.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            return cursor;
        }
        // Método para eliminar un calendario específico
        public void eliminarCalendario(String calendarioId) {
            SQLiteDatabase db = this.getWritableDatabase();

            // Eliminar las tareas asociadas al calendario
            db.delete(TareaEntrada.TABLE_NAME, TareaEntrada.COLUMN_CALENDARIO_ID + " = ?", new String[]{calendarioId});

            // Eliminar el calendario
            db.delete(CalendarioEntrada.TABLE_NAME, CalendarioEntrada._ID + " = ?", new String[]{calendarioId});

            db.close();
        }

        // Método para eliminar una tarea específica
        public void eliminarTarea(String tareaId) {
            SQLiteDatabase db = this.getWritableDatabase();
            String selection = TareaEntrada._ID + " = ?";
            String[] selectionArgs = { tareaId };
            // Ejecutar la consulta de eliminación
            db.delete(TareaEntrada.TABLE_NAME, selection, selectionArgs);
            db.close();
        }

    }
}


