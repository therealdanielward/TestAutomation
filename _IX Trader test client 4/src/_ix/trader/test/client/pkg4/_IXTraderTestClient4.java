package _ix.trader.test.client.pkg4;

import chrome.webdriver.console.error.log.pageLoadCore;

public class _IXTraderTestClient4
{

  public static void main(String[] args) 
  {
      pageLoadCore plc = new pageLoadCore();
      plc.setURL("http://cargofleet.ix.co.za/");
      plc.setClient("IXTradertestclient4");
      plc.createTable();
      plc.invokeBrowser();
      plc.getSitemapLinks();
      plc.processArrayList();
      
  }
  
}
