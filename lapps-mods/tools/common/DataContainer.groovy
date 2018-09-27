import static org.lappsgrid.discriminator.Discriminators.Uri

class DataContainer extends Data<Container> {
	public DataContainer() {
		this.discriminator = Uri.LAPPS		
	}
	
	public DataContainer(Container container) {
		this.discriminator = Uri.LAPPS
		this.payload = container
	}
}