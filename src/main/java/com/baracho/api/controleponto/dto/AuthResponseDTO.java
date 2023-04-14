package com.baracho.api.controleponto.dto;

public class AuthResponseDTO {
      private String accessToken;
      private String tokenType = "Bearer ";

      public AuthResponseDTO(String accessToken) {
            this.accessToken = accessToken;
      }

      public String getAccessToken() {
            return accessToken;
      }

      public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
      }

      public String getTokenType() {
            return tokenType;
      }

      public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
      }

      @Override
      public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
            result = prime * result + ((tokenType == null) ? 0 : tokenType.hashCode());
            return result;
      }

      @Override
      public boolean equals(Object obj) {
            if (this == obj)
                  return true;
            if (obj == null)
                  return false;
            if (getClass() != obj.getClass())
                  return false;
            AuthResponseDTO other = (AuthResponseDTO) obj;
            if (accessToken == null) {
                  if (other.accessToken != null)
                        return false;
            } else if (!accessToken.equals(other.accessToken))
                  return false;
            if (tokenType == null) {
                  if (other.tokenType != null)
                        return false;
            } else if (!tokenType.equals(other.tokenType))
                  return false;
            return true;
      }

      @Override
      public String toString() {
            return "AuthResponseDTO [accessToken=" + accessToken + ", tokenType=" + tokenType + "]";
      }


}
