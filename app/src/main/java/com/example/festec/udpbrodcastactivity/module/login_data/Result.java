package com.example.festec.udpbrodcastactivity.module.login_data;

/**
 * 包含数据或错误异常的结果成功的泛型类。
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        } else if (this instanceof Result.Failed) {
            Result.Failed failed = (Result.Failed) this;
            return "Failed[data=" + failed.getData().toString() + "]";
        }
        return "";
    }

    // Success sub-class
    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Failed sub-class
    public final static class Failed<T> extends Result {
        private T failedData;

        public Failed(T data) {
            this.failedData = data;
        }

        public T getData() {
            return this.failedData;
        }
    }

    // Error sub-class
    public final static class Error extends Result {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
