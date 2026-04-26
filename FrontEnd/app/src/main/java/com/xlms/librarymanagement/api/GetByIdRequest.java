package com.xlms.librarymanagement.api;

import com.google.gson.annotations.SerializedName;

public class GetByIdRequest {
    @SerializedName("ID")
    private String id;

    public GetByIdRequest(String id) {
        this.id = id;
    }
}
