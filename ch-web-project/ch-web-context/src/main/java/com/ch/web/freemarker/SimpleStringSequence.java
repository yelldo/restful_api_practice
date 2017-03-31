package com.ch.web.freemarker;

import com.ch.web.exception.WebException;
import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleStringSequence extends SimpleSequence {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public SimpleStringSequence(Collection<String> ls) {
		super(ls, new ObjectWrapper() {
			@Override
			public TemplateModel wrap(Object obj) throws TemplateModelException {
				return new SimpleScalar((String)obj);
			}
		});
	}
	public List<String> convertToList(boolean withsplit, boolean removeSamed){
		try {
			List<String> ls = new ArrayList<String>();
			for(int i=0; i<size(); i++){
				TemplateScalarModel tm = (TemplateScalarModel)get(i);
				String str = tm.getAsString();
				if(str != null){
					if(withsplit){
						for(String mstr : str.split(",")){
							if(StringUtils.isBlank(mstr)){
								continue;
							}
							if(!removeSamed || !ls.contains(mstr))
								ls.add(mstr);
						}
					}else{
						if(!removeSamed || !ls.contains(str))
							ls.add(str);
					}
				}
			}
			return ls;
		} catch (Exception e) {
			throw new WebException("Convert to string list error", e);
		}
	}
}
