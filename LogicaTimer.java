public boolean isAberta() {
    return LocalDateTime.now().isBefore(this.dataEncerramento);
}

