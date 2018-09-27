package org.apache.ctakes.gui.dictionary.umls;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 12/10/2015
 */
public enum Tui {
   // Activities & Behaviors
   T052( "Activity" ),
   T053( "Behavior" ),
   T056( "Daily or Recreational Activity" ),
   T051( "Event" ),
   T064( "Governmental or Regulatory Activity" ),
   T055( "Individual Behavior" ),
   T066( "Machine Activity" ),
   T057( "Occupational Activity" ),
   T054( "Social Behavior" ),
   // Anatomy
   T017( "Anatomical Structure" ),
   T029( "Body Location or Region" ),
   T023( "Body Part, Organ, or Organ Component" ),
   T030( "Body Space or Junction" ),
   T031( "Body Substance" ),
   T022( "Body System" ),
   T025( "Cell" ),
   T026( "Cell Component" ),
   T018( "Embryonic Structure" ),
   T021( "Fully Formed Anatomical Structure" ),
   T024( "Tissue" ),
   // Chemicals & Drugs
   T116( "Amino Acid, Peptide, or Protein" ),
   T195( "Antibiotic" ),
   T123( "Biologically Active Substance" ),
   T122( "Biomedical or Dental Material" ),
   T118( "Carbohydrate" ),
   T103( "Chemical" ),
   T120( "Chemical Viewed Functionally" ),
   T104( "Chemical Viewed Structurally" ),
   T200( "Clinical Drug" ),
   T111( "Eicosanoid" ),
   T196( "Element, Ion, or Isotope" ),
   T126( "Enzyme" ),
   T131( "Hazardous or Poisonous Substance" ),
   T125( "Hormone" ),
   T129( "Immunologic Factor" ),
   T130( "Indicator, Reagent, or Diagnostic Aid" ),
   T197( "Inorganic Chemical" ),
   T119( "Lipid" ),
   T124( "Neuroreactive Substance or Biogenic Amine" ),
   T114( "Nucleic Acid, Nucleoside, or Nucleotide" ),
   T109( "Organic Chemical" ),
   T115( "Organophosphorus Compound" ),
   T121( "Pharmacologic Substance" ),
   T192( "Receptor" ),
   T110( "Steroid" ),
   T127( "Vitamin" ),
   // Concepts & Ideas
   T185( "Classification" ),
   T077( "Conceptual Entity" ),
   T169( "Functional Concept" ),
   T102( "Group Attribute" ),
   T078( "Idea or Concept" ),
   T170( "Intellectual Product" ),
   T171( "Language" ),
   T080( "Qualitative Concept" ),
   T081( "Quantitative Concept" ),
   T089( "Regulation or Law" ),
   T082( "Spatial Concept" ),
   T079( "Temporal Concept" ),
   // Devices
   T203( "Drug Delivery Device" ),
   T074( "Medical Device" ),
   T075( "Research Device" ),
   // Disorders
   T020( "Acquired Abnormality" ),
   T190( "Anatomical Abnormality" ),
   T049( "Cell or Molecular Dysfunction" ),
   T019( "Congenital Abnormality" ),
   T047( "Disease or Syndrome" ),
   T050( "Experimental Model of Disease" ),
   T033( "Finding" ),
   T037( "Injury or Poisoning" ),
   T048( "Mental or Behavioral Dysfunction" ),
   T191( "Neoplastic Process" ),
   T046( "Pathologic Function" ),
   T184( "Sign or Symptom" ),
   // Genes & Molecular Sequences
   T087( "Amino Acid Sequence" ),
   T088( "Carbohydrate Sequence" ),
   T028( "Gene or Genome" ),
   T085( "Molecular Sequence" ),
   T086( "Nucleotide Sequence" ),
   // Geographic Areas
   T083( "Geographic Area" ),
   // Living Beings
   T100( "Age Group" ),
   T011( "Amphibian" ),
   T008( "Animal" ),
   T194( "Archaeon" ),
   T007( "Bacterium" ),
   T012( "Bird" ),
   T204( "Eukaryote" ),
   T099( "Family Group" ),
   T013( "Fish" ),
   T004( "Fungus" ),
   T096( "Group" ),
   T016( "Human" ),
   T015( "Mammal" ),
   T001( "Organism" ),
   T101( "Patient or Disabled Group" ),
   T002( "Plant" ),
   T098( "Population Group" ),
   T097( "Professional or Occupational Group" ),
   T014( "Reptile" ),
   T010( "Vertebrate" ),
   T005( "Virus" ),
   // Objects
   T071( "Entity" ),
   T168( "Food" ),
   T073( "Manufactured Object" ),
   T072( "Physical Object" ),
   T167( "Substance" ),
   // Occupations
   T091( "Biomedical Occupation or Discipline" ),
   T090( "Occupation or Discipline" ),
   // Organizations
   T093( "Health Care Related Organization" ),
   T092( "Organization" ),
   T094( "Professional Society" ),
   T095( "Self-help or Relief Organization" ),
   // Phenomena
   T038( "Biologic Function" ),
   T069( "Environmental Effect of Humans" ),
   T068( "Human-caused Phenomenon or Process" ),
   T034( "Laboratory or Test Result" ),
   T070( "Natural Phenomenon or Process" ),
   T067( "Phenomenon or Process" ),
   // Physiology
   T043( "Cell Function" ),
   T201( "Clinical Attribute" ),
   T045( "Genetic Function" ),
   T041( "Mental Process" ),
   T044( "Molecular Function" ),
   T032( "Organism Attribute" ),
   T040( "Organism Function" ),
   T042( "Organ or Tissue Function" ),
   T039( "Physiologic Function" ),
   // Procedures
   T060( "Diagnostic Procedure" ),
   T065( "Educational Activity" ),
   T058( "Health Care Activity" ),
   T059( "Laboratory Procedure" ),
   T063( "Molecular Biology Research Technique" ),
   T062( "Research Activity" ),
   T061( "Therapeutic or Preventive Procedure" ),
   // ERROR
   T999( "Error" );

   final private String _description;

   Tui( final String description ) {
      _description = description;
   }

   public String getDescription() {
      return _description;
   }

   public int getIntValue() {
      return Integer.parseInt( name().substring( 1 ) );
   }

//   static public Tui valueOf( final String text ) {
//
//
//
//      for ( Tui tuiEnum : Tui.values() ) {
//         if ( tuiEnum.name().equals( text ) ) {
//            return tuiEnum;
//         }
//      }
//      return Tui.T999;
//   }

}
