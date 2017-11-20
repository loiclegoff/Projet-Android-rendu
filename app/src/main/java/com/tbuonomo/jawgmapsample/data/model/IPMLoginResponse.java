
package com.tbuonomo.jawgmapsample.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by llgle on 19/11/2017.
 */

public class IPMLoginResponse {

        @SerializedName("token")
        @Expose
        private String token;
        @SerializedName("success")
        @Expose
        private Boolean success;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }


}
