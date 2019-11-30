int sum(int a, int b) {
    return a+b;
}

int sub(int a, int b) {
    return sum(a, -b);
}
void main() {
    int a = 3;
    int b = 5;
    int c = 0;
    while(b<10) {
        ++b;
        if(a or c) {
            _print(a);
            a = sum(a, -1);
            c = sub(b, 2);
        }
    }
    return;
}