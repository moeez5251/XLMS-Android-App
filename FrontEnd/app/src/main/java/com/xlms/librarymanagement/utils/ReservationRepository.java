package com.xlms.librarymanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xlms.librarymanagement.model.Reservation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private static final String PREF_NAME = "reservation_prefs";
    private static final String KEY_RESERVATIONS = "reservations";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ReservationRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addReservation(Reservation reservation) {
        List<Reservation> reservations = getAllReservations();
        reservations.add(0, reservation);
        saveReservations(reservations);
    }

    public List<Reservation> getAllReservations() {
        String json = sharedPreferences.getString(KEY_RESERVATIONS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Reservation>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public List<Reservation> getReservationsByUser(String email) {
        List<Reservation> allReservations = getAllReservations();
        List<Reservation> userReservations = new ArrayList<>();
        for (Reservation reservation : allReservations) {
            if (reservation.getUserEmail() != null && reservation.getUserEmail().equals(email)) {
                userReservations.add(reservation);
            }
        }
        return userReservations;
    }

    private void saveReservations(List<Reservation> reservations) {
        String json = gson.toJson(reservations);
        sharedPreferences.edit().putString(KEY_RESERVATIONS, json).apply();
    }
}
