package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ColumnRequest {
    @SerializedName("column")
    private List<String> column;

    public ColumnRequest(List<String> column) {
        this.column = column;
    }
}
