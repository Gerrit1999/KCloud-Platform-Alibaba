package org.laokou.common.i18n.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author laokou
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Datas<T> extends DTO {

	private long total;

	private List<T> records;

}
