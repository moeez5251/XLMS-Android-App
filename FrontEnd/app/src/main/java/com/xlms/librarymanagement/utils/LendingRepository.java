package com.xlms.librarymanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xlms.librarymanagement.model.LendedBook;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple repository using SharedPreferences to persist lended books.
 * In a real app, this would use a database (Room/SQLite) or an API.
 */
public class LendingRepository {
    private static final String PREF_NAME = "lending_prefs";
    private static final String KEY_LENDINGS = "lended_books";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public LendingRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addLending(LendedBook book) {
        List<LendedBook> books = getAllLendings();
        books.add(0, book); // Add to beginning of list
        saveLendings(books);
    }

    public List<LendedBook> getAllLendings() {
        String json = sharedPreferences.getString(KEY_LENDINGS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<LendedBook>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public List<LendedBook> getLendingsByUser(String email) {
        List<LendedBook> allBooks = getAllLendings();
        List<LendedBook> userBooks = new ArrayList<>();
        for (LendedBook book : allBooks) {
            if (book.getUserId() != null && book.getUserId().equals(email)) {
                userBooks.add(book);
            }
        }
        return userBooks;
    }

    private void saveLendings(List<LendedBook> books) {
        String json = gson.toJson(books);
        sharedPreferences.edit().putString(KEY_LENDINGS, json).apply();
    }
}
