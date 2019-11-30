int add(int x, int y) {
   int z;
   z = x+y;
   return z;
}

void main () {
   int t = 10;
   while(! ((t-2)<5)) {
       _print(add(0,t));
       if (t>10) {
       --t;
       }
   }
}