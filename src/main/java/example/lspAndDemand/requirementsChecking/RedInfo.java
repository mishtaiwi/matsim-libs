package example.lspAndDemand.requirementsChecking;

import lsp.LSPInfo;

/*package-private*/ class RedInfo extends LSPInfo {

	/*package-private*/ RedInfo() {
		this.getAttributes().putAttribute( "red", null );
	}

	@Override
	public String getName() {
		return "red";
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
	}

}
