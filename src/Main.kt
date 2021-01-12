import service.ApiClient

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ApiClient().getLeagues()
        }
    }
}