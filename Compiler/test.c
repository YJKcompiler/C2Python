int add(int x, int y) {
	int z = 10;
	while(z>0){
	    z = z - 1;
	}
	z = x + y + z;
	return z;
}

void main () {
	int t = 33;  
	_print(add(1,t));
}
