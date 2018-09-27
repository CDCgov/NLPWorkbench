import os, shutil, collections
from itertools import groupby
from operator import itemgetter
from hl7parser import HL7Segment
import glob

section_header_dic = {'RR': 'Reference Range', 'PS': 'Primary Site', 'NS': 'Nature of Specimen',
					  'GP': 'Gross Pathology', 'MP': 'Micro Pathology', 'CH': 'Clinical History',
					  'FD': 'Final Diagnosis', 'CM': 'Comment Section', 'SR': 'Supplemental Reports',
					  'HI': 'Histology ICD-O-3', 'TD': 'Text Diagnosis'}


def covert_doc_text(doc_text):
	'''Replace characters'''
	doc_text = doc_text.replace("\\E\\X0D", '')
	doc_text = doc_text.replace("\\E\\X0A", '\n')
	doc_text = doc_text.replace('\\E\\X09', '\t')

	doc_text = doc_text.replace("\\X0D\\", '')
	doc_text = doc_text.replace("\\F\\", ' ')    
	doc_text = doc_text.replace("\\X0A\\", '\n')
	doc_text = doc_text.replace('\\X09\\', '\t')

	doc_text = doc_text.replace('\\E\\', '')
	doc_text = doc_text.replace('  ', ' ')
	doc_text = doc_text.replace(' \n', '\n')
	doc_text = doc_text.replace('\n\n', '\n')

	return doc_text

def get_report(s):
	#return "".join(i for i in s if ord(i)<128)
	cleaned_text = ""
	for char in s :
		if ord(char) >= 128 or ord(char) < 9 or 10 < ord(char) <=31 :
			cleaned_text = cleaned_text+ " "
		else:
			cleaned_text = cleaned_text+char
	return cleaned_text

def parse_hl7_file(text):

	#hl7_fp = file(hl7_file_path, "rb")
	doc = text
	doc_temps = doc.splitlines()
	part_1 = ''
	part_2 = ''
	result = ''
	doc_section_dic = {}
	temp_dic = {}
	obr_indexes = []
	obr_dic = {}
	for i, doc_temp in enumerate(doc_temps):
		try:
			segment = HL7Segment(doc_temp)            
			if segment.type == 'OBR':                
				obr_indexes.append(segment.set_id[0])
		except:
			pass

	if len(obr_indexes) > 1:
		for obr_index in obr_indexes:
			obr_dic[obr_index] = {}
	   
	for i, doc_temp in enumerate(doc_temps):
		try:
			segment = HL7Segment(doc_temp)
			if segment.type == 'OBX':
				doc_temp = doc_temp.replace('~','')
				segment = HL7Segment(doc_temp)
				
				if segment.set_id[0]:
					if segment.value_type[0] in ['FT', 'TX', 'ST']:
						section_heading = ''
						section_content = ''
						section_index = ''
						section_code = ''
						sub_id = segment.observation_sub_id[0]
						section_content = segment.observation_value[0][0]
						if len(segment.observation_identifier) == 1:
							section_heading = segment.observation_identifier[0]
						else:
							section_code = segment.observation_identifier[0][0]
							section_heading = segment.observation_identifier[1][0]

						if '.' in sub_id:
							obr_index = sub_id.split('.')[0]
							if obr_index in obr_dic:                                                
								section_index = obr_index

						else:
							obr_index = sub_id
							if obr_index in obr_dic:                                                
								section_index = obr_index                       
						 
						section_content = covert_doc_text(section_content).strip()
						
						if section_content:
							flag = False
							if section_heading in section_header_dic:
								
								section_heading = section_header_dic[section_heading]
								flag = True
							elif section_code in ['22636-5', '22638-1', '22637-3', '22634-0', '31205-8',
												  '22635-7', '22633-2', '21855-2', '19147-8', '22639-9',
												  '33746-9']:
								flag = True
							if not flag:                                
								pass
							
							if section_index:
								if section_heading not in obr_dic[section_index]:
									obr_dic[section_index][section_heading] = {}
									obr_dic[section_index][section_heading][i] = section_content
								else:
									obr_dic[section_index][section_heading][i] = section_content
							else:
								if section_heading not in temp_dic:
									temp_dic[section_heading] = {}
									temp_dic[section_heading][i] = section_content

								else:
									temp_dic[section_heading][i] = section_content
						else:
							part_1 += doc_temp + '\n'
					else:
						part_1 += doc_temp + '\n'
				else:
					part_1 += doc_temp + '\n'
			else:
				part_1 += doc_temp + '\n'
		except:
			part_1 += doc_temp + '\n'

	if obr_dic:
		#multiple reports grouped by OBR
		for section_index in sorted(obr_dic.keys()):
			doc_section_dic[section_index] = {}
			for section_heading in obr_dic[section_index]:
				seg_indexs = sorted(obr_dic[section_index][section_heading].keys())
				seg_group_list = []
				doc_section_dic[section_index][section_heading] = {}
				for k, g in groupby(enumerate(seg_indexs), lambda (i, x): i-x):
					seg_group_list.append(map(itemgetter(1), g))
				for j, seg_group in enumerate(seg_group_list):
					doc_section_dic[section_index][section_heading][j] = ''
					for seg_index in seg_group:
						doc_section_dic[section_index][section_heading][j] += obr_dic[section_index][section_heading][seg_index] + '\n'

		for section_index in sorted(doc_section_dic.keys()):
			for section_heading in doc_section_dic[section_index]:
				for index in sorted(doc_section_dic[section_index][section_heading].keys()):
					part_2 += section_heading + '\n\n'
					part_2 += doc_section_dic[section_index][section_heading][index] + '\n\n'
			part_2 += '\n\n'

	elif temp_dic:

		for section_heading in temp_dic:
			seg_indexs = sorted(temp_dic[section_heading].keys())
			seg_group_list = []
			doc_section_dic[section_heading] = {}
			for k, g in groupby(enumerate(seg_indexs), lambda (i, x): i-x):
				seg_group_list.append(map(itemgetter(1), g))
			for j, seg_group in enumerate(seg_group_list):
				doc_section_dic[section_heading][j] = ''
				for seg_index in seg_group:
					doc_section_dic[section_heading][j] += temp_dic[section_heading][seg_index] + '\n'

		for section_heading in doc_section_dic:
			for index in sorted(doc_section_dic[section_heading].keys()):
				part_2 += section_heading + '\n\n'
				part_2 += doc_section_dic[section_heading][index] + '\n\n'

	# part_1: structured part,  part_2: free-text part
	if part_2:
		part_1 = get_report(part_1)
		part_2 = get_report(part_2)
		result = part_1 + '\n\n\n\n\n\n' + part_2

	result = get_report(result)

	return result

if __name__ == '__main__':
	hl7_file_path = 'Batch_4_hl7/Batch_4_Breast_4054.hl7'
	hl7_file_list = glob.glob(hl7_file_path)
	for hl7_file_name in hl7_file_list:
		hl7_file_name = hl7_file_name.replace('\\','/')
		file_num = str(hl7_file_name).split('/')[1].split('.')[0]
		print file_num 
		text_file_path = 'CDC_txt/' + file_num + '.txt'
		result = parse_hl7_file(hl7_file_name)
		print(result)
		resultFile = open(text_file_path, 'w+')
		resultFile.write(result)
		resultFile.close()        
