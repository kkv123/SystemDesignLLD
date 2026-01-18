package StateDesignMachine;

interface VendingMachineFuction{
    void insertCoin(VendingMachineManager vm, int coin);
    void selectProduct(VendingMachineManager vm,int prodCnt);

    void dispenseItems(VendingMachineManager vm, int cnt);

    void refilItem(VendingMachineManager vm,int m);

    public String  getStatus();
}

class NoCoinStatus implements VendingMachineFuction{
    @Override
    public void insertCoin(VendingMachineManager vm, int coin) {
        System.out.println("curr "+vm.getCoin());
        vm.setCoin(vm.getCoin()+coin);
        System.out.println("coin added Balance is : "+vm.getCoin());


        vm.setCurrState(vm.getHasCoinStatus());
    }

    @Override
    public void selectProduct(VendingMachineManager vm, int product) {
        System.out.println("please enter coins");
    }

    @Override
    public void dispenseItems(VendingMachineManager vm,int prod) {
        System.out.println("please enter coins");
    }

    @Override
    public void refilItem(VendingMachineManager vm,int m) {
        System.out.println("please enter coins");
    }

    @Override
    public String  getStatus(){
        return "NO COIN";
    }
}

class HasCoinStatus implements VendingMachineFuction{

    @Override
    public void insertCoin(VendingMachineManager vm, int coin) {
        int currCoin= coin+ vm.getCoin();
        vm.setCoin(currCoin);
        System.out.println("current Balance is "+currCoin);
    }

    @Override
    public void selectProduct(VendingMachineManager vm,int prodCnt) {
        if(prodCnt>vm.productCnt){
            System.out.println("Pls select less no. of product .");
        }
        int currProd= vm.getProductCnt()-prodCnt;
        vm.setProductCnt(currProd);

        System.out.println("Selected number of product: "+prodCnt +" remaining :"+currProd);
        vm.setCurrState(vm.getDispenseItemState());
    }

    @Override
    public void dispenseItems(VendingMachineManager vm, int prod) {
        System.out.println("Pls select the product first");
    }

    @Override
    public void refilItem(VendingMachineManager vm,int m) {
        System.out.println("Pls select the product first");
    }

    @Override
    public String  getStatus(){
        return "HAS COIN";
    }
}

class DispenseItemState implements VendingMachineFuction{

    @Override
    public void insertCoin(VendingMachineManager vm, int coin) {
        System.out.println("Despensing product can't add coin");
    }

    @Override
    public void selectProduct(VendingMachineManager vm, int prodCnt) {
        System.out.println("Despensing product can't add coin");
    }

    @Override
    public void dispenseItems(VendingMachineManager vm, int cnt) {
        System.out.println("Successfully despensed product :"+ cnt);
        if(vm.getProductCnt()<=0){
            vm.setCurrState(vm.getRefillState());
            System.out.println("Pls refil product again ");
        }else{
            vm.setCurrState(vm.getNoCoinStatus());
            System.out.println("Pls enter coins");
        }

    }

    @Override
    public void refilItem(VendingMachineManager vm,int cnt) {
        System.out.println("despensing state can't - refill");
    }

    @Override
    public String  getStatus(){
        return "DESPENCE STATE";
    }
}

class RefillState implements VendingMachineFuction{


    @Override
    public void insertCoin(VendingMachineManager vm, int coin) {
        System.out.println("In refill state - coin can't be accepted ");
    }

    @Override
    public void selectProduct(VendingMachineManager vm,int prod) {
        System.out.println("In refill state - product can't be selected ");
    }

    @Override
    public void dispenseItems(VendingMachineManager vm,int prod) {
        System.out.println("In refill state ");
    }

    @Override
    public void refilItem(VendingMachineManager vm,int cnt) {
        vm.setProductCnt(cnt);
      vm.setCurrState(vm.getNoCoinStatus());
    }

    @Override
    public String  getStatus(){
        return "REFILL COIN";
    }
}

class VendingMachineManager{


    int coin;
    int productCnt;
    int selectedProd;

    private VendingMachineFuction currState;
    private NoCoinStatus noCoinStatus;


    private HasCoinStatus hasCoinStatus;
    private DispenseItemState dispenseItemState;
    private RefillState refillState;

    public VendingMachineManager(int product){
        this.productCnt= product;
        this.coin=0;
        this.selectedProd=0;

        noCoinStatus = new NoCoinStatus();
        hasCoinStatus= new HasCoinStatus();
        dispenseItemState= new DispenseItemState();
        refillState= new RefillState();

        currState = noCoinStatus;
    }

    void insertCoin(int coin){

//        System.out.println("coin added success : current Balance -"+coin);
        currState.insertCoin(this,coin);
    }

    void selectProduct(int count){
      currState.selectProduct(this,count);
      selectedProd = count;
    }

    void dispenceProduct(){
        currState.dispenseItems(this,selectedProd);
    }

    void refillItems(int prodCnt){
        setProductCnt(prodCnt);
        System.out.println("product refilled : "+prodCnt);
    }

    public String  getStatus(){
        return currState.getStatus();
    }

//    ------------------------Getter & Setters--------------------------

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public void setProductCnt(int productCnt) {
        this.productCnt = productCnt;
    }

    public VendingMachineFuction getCurrState() {
        return currState;
    }

    public void setCurrState(VendingMachineFuction currState) {
        this.currState = currState;
    }
    public HasCoinStatus getHasCoinStatus() {
        return hasCoinStatus;
    }

    public DispenseItemState getDispenseItemState() {
        return dispenseItemState;
    }

    public int getCoin() {
        return coin;
    }

    public int getProductCnt() {
        return productCnt;
    }

    public NoCoinStatus getNoCoinStatus() {
        return noCoinStatus;
    }

    public RefillState getRefillState() {
        return refillState;
    }

}
public class VendingMachineMain1 {
    public static void main(String[] args) {
        VendingMachineManager vendingMachineManager= new VendingMachineManager(10);
        while(true){
            System.out.println("Status is "+vendingMachineManager.getStatus());
            vendingMachineManager.insertCoin(5);
            System.out.println("Status is "+vendingMachineManager.getStatus());
            vendingMachineManager.selectProduct(4);
            System.out.println("Status is "+vendingMachineManager.getStatus());
            vendingMachineManager.dispenceProduct();
        }


    }
}
